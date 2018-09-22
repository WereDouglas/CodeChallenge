package com.were.douglas.codechallenge;

import com.were.douglas.codechallenge.Model.Ai;
import com.were.douglas.codechallenge.Model.Element;
import com.were.douglas.codechallenge.Model.Lib;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Helper {

    public static HashMap<Float, Float> fileHash = new HashMap<Float, Float>();
    public static HashMap<Float, Double> NewfileHash = new HashMap<Float, Double>();
    public static ArrayList<Element> elementList;
    public static ArrayList<Ai> aiList;
    public static ArrayList<Lib> libList;
    public static Float min;
    public static Float max ;

    public  static List<Float> FileListIntensity = new ArrayList<>();


    public static double normalise(double d) {

        return roundDouble(((d-min)/(max-min)),2);
    }
    public static double roundDouble(double d, int decimalPlace) {

        return BigDecimal.valueOf(d).setScale(decimalPlace,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    public static float round(float d, int decimalPlace) {

        return BigDecimal.valueOf(d).setScale(decimalPlace,BigDecimal.ROUND_HALF_UP).floatValue();
    }


}
