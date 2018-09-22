package com.were.douglas.codechallenge;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.were.douglas.codechallenge.Model.Ai;
import com.were.douglas.codechallenge.Model.Element;
import com.were.douglas.codechallenge.Model.Lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.were.douglas.codechallenge.Helper.aiList;
import static com.were.douglas.codechallenge.Helper.elementList;
import static com.were.douglas.codechallenge.Helper.libList;

public class ChatActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        GraphView graph = (GraphView) findViewById(R.id.graph);
      //  Arrays.sort(aiList);

        DataPoint[] dataPointsLib = new DataPoint[libList.size()];
        DataPoint[] dataPointsAi = new DataPoint[aiList.size()];
        /**  int i = 0;
         for (Element t : elementList) {
         System.out.println("NAME: "+ t.getName() + " FREQ: " + t.getFreq()+ " LIB: " +t.getLib() + " AI: " + t.getAi()  );
         // dataPoints[i++] = new DataPoint(Float.parseFloat(t.getLib() + ""), Float.parseFloat( +t.getFreq()+ "")); // not sure but I think the second argument should be of type double
         dataPoints[i++] = new DataPoint(i++, Float.parseFloat( +t.getFreq()+ "")); // not sure but I think the second argument should be of type double

         }**/


        Collections.sort(aiList, new Comparator<Ai>() {
            @Override
            public int compare(Ai lhs, Ai rhs) {
                return lhs.getIns().compareTo(rhs.getIns());
            }
        });
        Collections.sort(libList, new Comparator<Lib>() {

            @Override
            public int compare(Lib lhs, Lib rhs) {
                return lhs.getIns().compareTo(rhs.getIns());
            }
        });

        System.out.println("LIB SIZE: "+ libList.size() );
        System.out.println("AI SIZE: "+ aiList.size() );


        for (int p = 0; p < libList.size(); p++) {

            System.out.println("LIB : "+ libList.get(p).getIns() );

                  dataPointsLib[p] = new DataPoint(Double.parseDouble(libList.get(p).getIns()+""),Double.parseDouble( libList.get(p).getWv()+"")); // not sure but I think the second argument should be of type double

        }
        for (int ps = 0; ps < aiList.size(); ps++) {

            System.out.println("AI : "+ aiList.get(ps).getIns() );

                 dataPointsAi[ps] = new DataPoint(Double.parseDouble(aiList.get(ps).getIns()+""),Double.parseDouble( aiList.get(ps).getWv()+"")); // not sure but I think the second argument should be of type double

        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPointsAi);
        series.setColor(Color.GREEN);
        graph.addSeries(series);

        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(dataPointsLib);
        series2.setColor(Color.BLUE);
        graph.addSeries(series2);


        // set manual X bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-150);
        graph.getViewport().setMaxY(150);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(4);
        graph.getViewport().setMaxX(80);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        

        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling


    }

}
