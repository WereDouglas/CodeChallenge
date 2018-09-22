package com.were.douglas.codechallenge;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Browser;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.were.douglas.codechallenge.Model.Ai;
import com.were.douglas.codechallenge.Model.Element;
import com.were.douglas.codechallenge.Model.Lib;

import java.io.Console;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.were.douglas.codechallenge.Helper.FileListIntensity;
import static com.were.douglas.codechallenge.Helper.aiList;
import static com.were.douglas.codechallenge.Helper.elementList;
import static com.were.douglas.codechallenge.Helper.libList;
import static com.were.douglas.codechallenge.Helper.max;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    ListView lv, lv2, lv3;
    ArrayAdapter adapter_name;
    ArrayAdapter adapter_spec;
    ArrayAdapter adapter_res;
    Button browse, eval, chart,load;
    boolean loaded = false;

    public HashMap<Float, HashMap<String, Float>> DbHash = new HashMap<Float, HashMap<String, Float>>();
    public HashMap<String, Float> FoundHash = new HashMap<String, Float>();

    String[] nextLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.listView);
        lv2 = (ListView) findViewById(R.id.listView2);
        lv3 = (ListView) findViewById(R.id.listView3);
        mTextMessage = (TextView) findViewById(R.id.message);
        adapter_name = new ArrayAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item);
        adapter_spec = new ArrayAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item);
        adapter_res = new ArrayAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item);
        browse = (Button) findViewById(R.id.browse);
        load = (Button) findViewById(R.id.load);
        eval = (Button) findViewById(R.id.evaluate);
        chart = (Button) findViewById(R.id.chart);
        if (!loaded) {
            eval.setVisibility(View.GONE);
            chart.setVisibility(View.GONE);
        } else {
            eval.setVisibility(View.VISIBLE);

        }
        chart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startLocation = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(startLocation);

            }
        });
        load.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                 //Intent filePickerIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Files.getContentUri("external"));
                Intent filePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                filePickerIntent.setType("*/*");
                startActivityForResult(filePickerIntent, 0);


            }
        });

        String filename;

        browse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                 aiList = new ArrayList<Ai>();
                Helper.fileHash = new HashMap<Float, Float>();
                try {
                    File csvfile = new File(Environment.getExternalStorageDirectory() + "/Al_2024.csv");
                    System.out.println(Environment.getExternalStorageDirectory() + "/Al_2024.csv");
                    CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));
                    String[] nextLine;
                    while ((nextLine = reader.readNext()) != null) {
                        // nextLine[] is an array of values from the line
                        //  System.out.println( " "+nextLine[0] + " "+ nextLine[1] + " "+ nextLine[2]);
                        Ai ai = new Ai();
                        try {
                            Float val1 = Float.parseFloat(nextLine[1]);
                            Helper.fileHash.put(Helper.round(Float.parseFloat(nextLine[0]), 3), val1);

                            ai.setWv(Helper.roundDouble(Double.parseDouble(nextLine[0]+""),3));
                            ai.setIns(Double.parseDouble( val1+""));
                            aiList.add(ai);
                           FileListIntensity.add(val1);
                            Log.e("", " " + nextLine[0] + " " + (val1));
                            adapter_spec.add(Helper.round(Float.parseFloat(nextLine[0]), 3) + "\n-" + Float.parseFloat(nextLine[1]) + "");
                        } catch (Exception u) {

                            u.printStackTrace();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
                lv2.setAdapter(adapter_spec);
                Helper.min = Collections.min(FileListIntensity);
                Helper.max = Collections.max(FileListIntensity);

                System.out.println("Min: " + Helper.min + " Max: " + max);
                loaded = true;
                eval.setVisibility(View.VISIBLE);
                browse.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "CLICK EVALUATE TO COMPUTE", Toast.LENGTH_LONG).show();
            }
        });
        eval.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Helper.NewfileHash = new HashMap<Float, Double>();
                Helper.elementList = new ArrayList<Element>();
                if (!loaded) {
                    Toast.makeText(MainActivity.this, "Please load the file first ", Toast.LENGTH_LONG).show();
                    return;
                }
                Iterator it = Helper.fileHash.entrySet().iterator();
                while (it.hasNext()) {
                    Element el = new Element();
                    Map.Entry pair = (Map.Entry) it.next();
                    //   System.out.println(pair.getKey() + " = " + pair.getValue());
                    /*Lets normalise this value*/
                    Double val3 = Helper.normalise(Double.parseDouble(pair.getValue() + ""));
                    Helper.NewfileHash.put(Float.parseFloat(pair.getKey() + ""), val3);
                    if (DbHash.containsKey(pair.getKey())) {
                        System.out.println("WaveLength: " + pair.getKey() + " spectrum value : " + val3 + " valued at " + DbHash.get(pair.getKey()));
                        FoundHash.putAll(DbHash.get(pair.getKey()));

                        el.setAi(Double.parseDouble(val3 + ""));
                        el.setFreq(Double.parseDouble(pair.getKey() + ""));


                        Iterator itf = DbHash.get(pair.getKey()).entrySet().iterator();
                        while (itf.hasNext()) {
                            Map.Entry pairs = (Map.Entry) itf.next();
                            System.out.println("NAME: " + pairs.getKey() + " LIB INT VAL  " + pairs.getValue() + " FREQ: " + pair.getKey() + " AI_:" + val3);
                            // adapter_res.add(pairs.getKey() + "\n-" + pairs.getValue() + " " + (Double.parseDouble(pairs.getValue()+"")*100) +"%");
                            el.setLib(Double.parseDouble(pairs.getValue() + ""));
                            el.setName(pairs.getKey() + "");
                            elementList.add(el);
                            itf.remove();
                        }

                    } else {
                        //System.out.println("No ! no such value " + pair.getKey());
                    }

                    it.remove();

                }

                for (Element t : elementList) {
                    System.out.println(t.getName());
                    adapter_res.add(t.getName() + "\n-" + t.getLib() + " <" + (Double.parseDouble(t.getLib() + "") * 100) + "%>");
                }
                /**
                Iterator itf = FoundHash.entrySet().iterator();
                while (itf.hasNext()) {
                    Map.Entry pair = (Map.Entry) itf.next();
                    // System.out.println("NAME: " + pair.getKey() + "  VAL " + pair.getValue());
                    //   adapter_res.add(pair.getKey() + "\n-" + pair.getValue() + " " + (Double.parseDouble(pair.getValue()+"")*100) +"%");
                    itf.remove();
                }**/
                lv3.setAdapter(adapter_res);
                chart.setVisibility(View.VISIBLE);
            }

        });


        try {

           File csvfile = new File(Environment.getExternalStorageDirectory() + "/LIBS_LINES.csv");
            //File csvfile = new File(getAssets().open("LIBS_LINES.csv"));
            System.out.println(Environment.getExternalStorageDirectory() + "/LIBS_LINES.csv");
            CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));

            libList = new ArrayList<Lib>();
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                //  System.out.println( " "+nextLine[0] + " "+ nextLine[1] + " "+ nextLine[2]);
                Lib  lib = new Lib();
                DbHash.put(Float.parseFloat(nextLine[1]), new HashMap() {{
                    put(nextLine[0], Float.parseFloat(nextLine[2]));
                }});
                //  map.put(.0F, new HashMap(){{put(.0F,0);}});
                Log.e("", " " + nextLine[0] + " " + nextLine[1] + " " + nextLine[2]);
                adapter_name.add(nextLine[0] + "\n" + nextLine[1]);
                lib.setIns(Double.parseDouble( nextLine[2]+""));
                lib.setWv(Double.parseDouble( nextLine[1]+""));
                lib.setName(nextLine[0]);
                libList.add(lib);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
            createFile();
        }
        lv.setAdapter(adapter_name);
    }
    public  void createFile(){

        AssetManager am = this.getAssets();
        AssetFileDescriptor afd = null;
        try {
            afd = am.openFd( "LIBS_LINES.csv");

            // Create new file to copy into.
            File file = new File(Environment.getExternalStorageDirectory() + java.io.File.separator + "LIBS_LINES.csv");
            file.createNewFile();

            copyFdToFile(afd.getFileDescriptor(), file);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public static void copyFdToFile(FileDescriptor src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }
    String mediaPath;
    String filename;
    String filePath = "";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri tempUri = null;
        File finalFile = null;

        Uri uri = data.getData();
        ContentResolver cr = this.getContentResolver();
        String mime = cr.getType(uri);
        Toast.makeText(this, " file"+ uri, Toast.LENGTH_LONG).show();
        System.out.println("FILE: " + getFileName(uri) );

        System.out.println("FILE: " + uri);

        try {
            filename = getFileName(uri);
        } catch (Exception p) {
          //  Log.e("try catching new name ", filename);
        }

        filePath = Environment.getExternalStorageDirectory() + "/" +  filename;
        try {
            Helper.fileHash.clear();
            aiList = new ArrayList<Ai>();
            FileListIntensity.clear();
            File csvfile = new File(Environment.getExternalStorageDirectory() + "/" +  filename);
            System.out.println(filePath);
            CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));

            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                //  System.out.println( " "+nextLine[0] + " "+ nextLine[1] + " "+ nextLine[2]);
                Ai ai = new Ai();
                try {
                    Float val1 = Float.parseFloat(nextLine[1]);
                    Helper.fileHash.put(Helper.round(Float.parseFloat(nextLine[0]), 3), val1);

                    ai.setWv(Helper.roundDouble(Double.parseDouble(nextLine[0]+""),3));
                    ai.setIns(Double.parseDouble( val1+""));
                    aiList.add(ai);
                    FileListIntensity.add(val1);
                    Log.e("", " " + nextLine[0] + " " + (val1));
                    adapter_spec.add(Helper.round(Float.parseFloat(nextLine[0]), 3) + "\n-" + Float.parseFloat(nextLine[1]) + "");
                } catch (Exception u) {

                    u.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
        lv2.setAdapter(adapter_spec);
        Helper.min = Collections.min(FileListIntensity);
        Helper.max = Collections.max(FileListIntensity);
        lv2.setAdapter(adapter_spec);
        loaded = true;
        browse.setVisibility(View.GONE);
        eval.setVisibility(View.VISIBLE);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}
