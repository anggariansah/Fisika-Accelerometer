package com.solvedev.accelerometertest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class Graph extends AppCompatActivity implements SensorEventListener {

    // Widget
    private GraphView graph;
    private Button btnStart;

    // variabel yang akan digunakan pada grafik
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private double graphLastXValue = 0d;
    double graphYValue = 0d;
    private LineGraphSeries<DataPoint> mSeries;
    private ArrayList<Double> dataArray = new ArrayList<>();

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    float[] percepatan = new float[1000];
    float[] kecepatan = new float[1000];
    float[] posisi = new float[1000];
    float[] waktu = new float[1000];
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // initialization
        graph = (GraphView) findViewById(R.id.graph);
        btnStart = (Button) findViewById(R.id.btn_start);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        initGraph(graph);
    }

    private void initGraph(GraphView graph) {
        graph.getViewport().setXAxisBoundsManual(true);
        // minimal x
        graph.getViewport().setMinX(0);
        // maximal x
        graph.getViewport().setMaxX(30);

        graph.getViewport().setYAxisBoundsManual(true);
        // minimal y
        graph.getViewport().setMinY(-10);
        // maximal y
        graph.getViewport().setMaxY(10);

        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        // title di x
        graph.getGridLabelRenderer().setHorizontalAxisTitle("time (s)");
        // ukuran title
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(22);
        // title di y
        graph.getGridLabelRenderer().setVerticalAxisTitle("Percepatan");
        // ukuran title
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(22);

        // menghilangkan border di grafik
        graph.getViewport().setDrawBorder(false);

        // mengeset mSeries ke graph
        // mSeries ini berfungsi untuk menampung data dan kemudian digambar menjadi titik di grafik
        mSeries = new LineGraphSeries<>();
        graph.addSeries(mSeries);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                waktu[index] = speed;

                if (speed > SHAKE_THRESHOLD) {

                }

                last_x = x;
                last_y = y;
                last_z = z;


                percepatan[index] = y;


                kecepatan[0] = 0;
                posisi[0] = 0;

                if(index == 0){
                    kecepatan[index] = 0 + percepatan[index] + percepatan[index+1] / 2 * waktu[index+1] - waktu[index];
                    posisi[index] = 0 + kecepatan[index] + kecepatan[index+1] / 2 * waktu[index+1] - waktu[index];
                }else {
                    kecepatan[index] = kecepatan[index-1] + percepatan[index] + percepatan[index+1] / 2 * waktu[index+1] - waktu[index];
                    posisi[index] = posisi[index-1] + kecepatan[index] + kecepatan[index+1] / 2 * waktu[index+1] - waktu[index];
                }

                float value = percepatan[index];

                graphYValue = value;

                // menambahkan data ke dalam array untuk nanti disimpan ke database
                dataArray.add(graphYValue);

                // bagian ini untuk menambah titik pada grafik
                mSeries.appendData(new DataPoint(graphLastXValue, graphYValue), true, 1000);

                // bagian ini untuk mengatur penambahan nilai x
                graphLastXValue += waktu[index];

                index++;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }


    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

}
