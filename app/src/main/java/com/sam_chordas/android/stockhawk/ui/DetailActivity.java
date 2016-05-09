package com.sam_chordas.android.stockhawk.ui;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sam_chordas.android.stockhawk.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Niko on 5/7/16.
 */
public class DetailActivity extends AppCompatActivity {

    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mChart = (LineChart) findViewById(R.id.chart);

        mChart.setDescription("");    // Hide the description
        mChart.getAxisLeft().setDrawLabels(false);
        mChart.getAxisRight().setDrawLabels(false);
        mChart.getXAxis().setDrawLabels(false);

        mChart.getLegend().setEnabled(false);   // Hide the legend

        String symbol = getIntent().getStringExtra(MyStocksActivity.SYMBOL_EXTRA);

        DownloadDataTask task = new DownloadDataTask();
        task.execute(new String[] { "http://ichart.finance.yahoo.com/table.csv?a=8&b=11&e=7&g=d&c=2014&d=4&f=2016&s=" + symbol });

    }

    private class DownloadDataTask extends AsyncTask<String, Void, LineData> {
        @Override
        protected LineData doInBackground(String... urls) {
            try {
                final ArrayList<Entry> valsComp1 = new ArrayList<>();
                final ArrayList<String> xVals = new ArrayList<>();

                URL url = new URL(urls[0]);
                URLConnection uc = url.openConnection();

                InputStreamReader inStream = new InputStreamReader(uc.getInputStream());
                BufferedReader buff = new BufferedReader(inStream);

                String line;

                int i = 0;

                while ((line = buff.readLine()) != null) {
                    if (i != 0) {
                        String[] data = line.split(",");
                        Entry c1e1 = new Entry(Float.parseFloat(data[4]), i);
                        valsComp1.add(c1e1);
                        xVals.add("");
                    }
                    i++;
                }

                for (Entry entry : valsComp1) {
                    entry.setXIndex(valsComp1.size() - entry.getXIndex());
                }

                LineDataSet setComp1 = new LineDataSet(valsComp1, "Company 1");
                setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
                Drawable drawable = getResources().getDrawable(R.drawable.gradient);
                setComp1.setFillDrawable(drawable);
                setComp1.setDrawFilled(true);
                setComp1.setCircleRadius(0);
                setComp1.setColor(Color.WHITE);

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(setComp1);

                return new LineData(xVals, dataSets);

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(LineData data) {
            mChart.setData(data);
            mChart.invalidate();
        }
    }
}
