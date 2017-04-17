package com.cyj.histogramview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    HistogramView histogramView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        histogramView = (HistogramView)findViewById(R.id.histogramView);
        ArrayList<String> nameLists = new ArrayList<>();
        ArrayList<String> countLists = new ArrayList<>();
        for (int i=0;i<8;i++){
            nameLists.add("项"+i);
            countLists.add(""+new Random().nextInt(20));
        }

        histogramView.start(nameLists,countLists);
    }
}
