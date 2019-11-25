package com.solvedev.accelerometertest;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Graph extends AppCompatActivity implements SensorEventListener {

    // Widget
    private GraphView graphPercepatan, graphKecepatan, graphPosisi;
    private Button btnStart;
    private TextView tvJumlah;

    // variabel yang akan digunakan pada grafik
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private double graphLastXValuePercepatan = 0d, graphLastXValueKecepatan = 0d, graphLastXValuePosisi = 0d;
    double graphYValuePercepatan = 0d, graphYValueKecepatan = 0d, graphYValuePosisi = 0d;
    private LineGraphSeries<DataPoint> mSeriesPercepatan, mSeriesKecepatan, mSeriesPosisi;
    private ArrayList<Double> arrayKecepatan = new ArrayList<>();
    private ArrayList<Double> arrayPercepatan = new ArrayList<>();
    private ArrayList<Double> arrayPosisi = new ArrayList<>();

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    float speed1, last_speed;
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
        graphPercepatan = (GraphView) findViewById(R.id.graph_percepatan);
        graphKecepatan = (GraphView) findViewById(R.id.graph_kecepatan);
        graphPosisi = (GraphView) findViewById(R.id.graph_posisi);
        tvJumlah = (TextView) findViewById(R.id.jumlah);
        btnStart = (Button) findViewById(R.id.btn_start);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_FASTEST);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                export();
            }
        });

        initGraphPercepatan(graphPercepatan);
        initGraphKecepatan(graphKecepatan);
        initGraphPosisi(graphPosisi);
    }

    private void initGraphPercepatan(GraphView graph) {
        graph.getViewport().setXAxisBoundsManual(true);
        // minimal x
        graph.getViewport().setMinX(0);
        // maximal x
        graph.getViewport().setMaxX(500);

        graph.getViewport().setYAxisBoundsManual(true);
        // minimal y
        graph.getViewport().setMinY(-15);
        // maximal y
        graph.getViewport().setMaxY(15);

        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        // title di x
        graph.getGridLabelRenderer().setHorizontalAxisTitle("time (s)");
        // ukuran title
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(15);
        // title di y
        graph.getGridLabelRenderer().setVerticalAxisTitle("Percepatan");
        // ukuran title
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(15);


//        graph.setScaleY(1f);
//        graph.setScaleX(1f);
//
        graph.setCameraDistance(0.1f);

        // menghilangkan border di grafik
        graph.getViewport().setDrawBorder(false);

        // mengeset mSeries ke graph
        // mSeries ini berfungsi untuk menampung data dan kemudian digambar menjadi titik di grafik
        mSeriesPercepatan = new LineGraphSeries<>();
        graph.addSeries(mSeriesPercepatan);
    }

    private void initGraphKecepatan(GraphView graph) {
        graph.getViewport().setXAxisBoundsManual(true);
        // minimal x
        graph.getViewport().setMinX(0);
        // maximal x
        graph.getViewport().setMaxX(500);

        graph.getViewport().setYAxisBoundsManual(true);
        // minimal y
        graph.getViewport().setMinY(-1000);
        // maximal y
        graph.getViewport().setMaxY(1000);

        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        // title di x
        graph.getGridLabelRenderer().setHorizontalAxisTitle("time (s)");
        // ukuran title
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(22);
        // title di y
        graph.getGridLabelRenderer().setVerticalAxisTitle("Kecepatan");
        // ukuran title
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(22);


        graph.setScaleY(1f);
        graph.setScaleX(1f);
//
//        graph.setCameraDistance(0.5f);

        // menghilangkan border di grafik
        graph.getViewport().setDrawBorder(false);

        // mengeset mSeries ke graph
        // mSeries ini berfungsi untuk menampung data dan kemudian digambar menjadi titik di grafik
        mSeriesKecepatan = new LineGraphSeries<>();
        graph.addSeries(mSeriesKecepatan);
    }

    private void initGraphPosisi(GraphView graph) {
        graph.getViewport().setXAxisBoundsManual(true);
        // minimal x
        graph.getViewport().setMinX(0);
        // maximal x
        graph.getViewport().setMaxX(500);

        graph.getViewport().setYAxisBoundsManual(true);
        // minimal y
        graph.getViewport().setMinY(10);
        // maximal y
        graph.getViewport().setMaxY(5000);

        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        // title di x
        graph.getGridLabelRenderer().setHorizontalAxisTitle("time (s)");
        // ukuran title
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(22);
        // title di y
        graph.getGridLabelRenderer().setVerticalAxisTitle("Posisi");
        // ukuran title
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(22);


//        graph.setScaleY(1f);
//        graph.setScaleX(1f);
//
//        graph.setCameraDistance(0.5f);

        // menghilangkan border di grafik
        graph.getViewport().setDrawBorder(false);

        // mengeset mSeries ke graph
        // mSeries ini berfungsi untuk menampung data dan kemudian digambar menjadi titik di grafik
        mSeriesPosisi = new LineGraphSeries<>();
        graph.addSeries(mSeriesPosisi);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 10) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(y  - last_y)/ diffTime * 10000;
                speed1 = Math.abs(y  + last_y) / 2 * diffTime;
                float position = (speed1 + last_speed) / 2 * diffTime;


                float speed2 =

                waktu[index] = diffTime;
                kecepatan[index] = speed1;
                posisi[index] = position;
                percepatan[index] = y;

                //float posisi = Math.abs(x + y + z - last_x)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {

                }

                last_y = y;
                last_speed = speed1;



                float valuePercepatan = percepatan[index];
                graphYValuePercepatan = valuePercepatan;
                // menambahkan data ke dalam array untuk nanti disimpan ke database
                arrayPercepatan.add(graphYValuePercepatan);
                // bagian ini untuk menambah titik pada grafik
                mSeriesPercepatan.appendData(new DataPoint(graphLastXValuePercepatan, graphYValuePercepatan), true, 1000);
                // bagian ini untuk mengatur penambahan nilai x
//                List<Float> a = new ArrayList<>();
//                a.add(percepatan[index]);
//                tvJumlah.setText(a.size());
                graphLastXValuePercepatan += 10;

                float valueKecepatan = kecepatan[index];
                graphYValueKecepatan = valueKecepatan;
                // menambahkan data  ke dalam array untuk nanti disimpan ke database
                arrayKecepatan.add(graphYValueKecepatan);
                // bagian ini untuk menambah titik pada grafik
                mSeriesKecepatan.appendData(new DataPoint(graphLastXValueKecepatan, graphYValueKecepatan), true, 1000);
                // bagian ini untuk mengatur penambahan nilai x
                graphLastXValueKecepatan += 10;

                float valuePosisi = posisi[index];
                graphYValuePosisi = valuePosisi;
                // menambahkan data ke dalam array untuk nanti disimpan ke database
                arrayPosisi.add(graphYValuePosisi);
                // bagian ini untuk menambah titik pada grafik
                mSeriesPosisi.appendData(new DataPoint(graphLastXValuePosisi, graphYValuePosisi), true, 1000);
                // bagian ini untuk mengatur penambahan nilai x
                graphLastXValuePosisi += 10;

                index++;
            }
        }

    }

    public void export(){

       StringBuilder data = new StringBuilder();
       data.append("Percepatan,Kecepatan,Posisi");

       for(int i = 0; i < 50;i++){
           data.append("\n"+percepatan[i]+","+kecepatan[i]+","+posisi[i]);
       }

        try{
            FileOutputStream fos = openFileOutput("data.csv",Context.MODE_PRIVATE);
            fos.write(data.toString().getBytes());
            fos.close();

            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "data.csv");
            Uri path = FileProvider.getUriForFile(context, "com.solvedev.accelerometertest.fileprovider",filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Send Email"));
        }catch (Exception e){
            e.printStackTrace();
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
