package com.were.douglas.codechallenge;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Browser;
import android.provider.MediaStore;
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
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    Button browse, eval, chart;
    boolean loaded = false;

    public HashMap<Float, HashMap<String, Float>> DbHash = new HashMap<Float, HashMap<String, Float>>();
    public HashMap<String, Float> FoundHash = new HashMap<String, Float>();

    String[] nextLine;


    List<Float> FileListIntensity = new ArrayList<>();

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


        String filename;

        browse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /** Intent filePickerIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Files.getContentUri("external"));
                 // filePickerIntent.setType("*//*");
               // startActivityForResult(filePickerIntent, 0); ****/

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
                    //  Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
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
                Helper.NewfileHash = new HashMap<Float, Float>();
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
                    Float val3 = Helper.normalise(Float.parseFloat(pair.getValue() + ""));
                    Helper.NewfileHash.put(Float.parseFloat(pair.getKey() + ""), val3);
                    if (DbHash.containsKey(pair.getKey())) {
                        System.out.println("WaveLength: " + pair.getKey() + " spectrum value : " + val3 + " valued at " + DbHash.get(pair.getKey()));
                        FoundHash.putAll(DbHash.get(pair.getKey()));

                        //  System.out.println("ANSWERS WV: " + pair.getKey() + " SPEC INT " + val3 + "  DB INT :"+ DbHash.get(pair.getKey()).get(val3));

                        // DbHash.get(pair.getKey()).get(val3);
                        //  Helper.ExistingHash.put(Double.parseDouble(pair.getKey()+""),Double.parseDouble(  DbHash.get(pair.getKey()).get(val3)+""));
                        //   Helper.SpectrumHash.put(Double.parseDouble(pair.getKey()+""),Double.parseDouble( val3+""));

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
                    adapter_res.add(t.getName() + "\n-" + t.getLib() + " " + (Double.parseDouble(t.getLib() + "") * 100) + "%");
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
        }
        lv.setAdapter(adapter_name);

    }

    String mediaPath;
    String filename;
    String filePath = "";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri tempUri = null;
        File finalFile = null;
        try {
            // When an Image is picked
            if (requestCode == 0 && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                tempUri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = MainActivity.this.getContentResolver().query(tempUri, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mediaPath = cursor.getString(columnIndex);
                // Set the Image in ImageView for Previewing the Media
                finalFile = new File(getRealPathFromURI(tempUri));
                cursor.close();
            } else {
                Toast.makeText(this, "You haven't picked a file", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
        try {
            filename = finalFile.getName();
        } catch (Exception p) {
            Log.e("try catching new name ", filename);
        }

        filePath = getApplicationContext().getFilesDir().getPath() + "/" + filename;
        // File f = new File(filePath);
        /*if (!f.exists())
        {
            try {
                f.createNewFile();
                util.copyFile(finalFile, f);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }*/
        try {
            File csvfile = new File(filePath);
            System.out.println(filePath);
            CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));

            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                //  System.out.println( " "+nextLine[0] + " "+ nextLine[1] + " "+ nextLine[2]);
                DbHash.put(Float.parseFloat(nextLine[1]), new HashMap() {{
                    put(nextLine[0], Float.parseFloat(nextLine[2]));
                }});
                //  map.put(.0F, new HashMap(){{put(.0F,0);}});
                Log.e("", " " + nextLine[0] + " " + nextLine[1] + " " + nextLine[2]);
                adapter_name.add(nextLine[0] + "\n" + nextLine[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
        lv.setAdapter(adapter_name);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

}
