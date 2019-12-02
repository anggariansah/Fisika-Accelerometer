package com.solvedev.accelerometertest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
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
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Graph extends AppCompatActivity implements SensorEventListener {

    // Widget
    private GraphView graphPercepatan, graphKecepatan, graphPosisi;
    private Button btnExport;
    private TextView tvJumlah;

    // variabel yang akan digunakan pada grafik
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private static final double GRAVITY = 9.807;

    private double graphLastXValuePercepatan = 0d, graphLastXValueKecepatan = 0d, graphLastXValuePosisi = 0d;
    double graphYValuePercepatan = 0d, graphYValueKecepatan = 0d, graphYValuePosisi = 0d;
    private LineGraphSeries<DataPoint> mSeriesPercepatan, mSeriesKecepatan, mSeriesPosisi;
    private ArrayList<Double> arrayKecepatan = new ArrayList<>();
    private ArrayList<Double> arrayPercepatan = new ArrayList<>();
    private ArrayList<Double> arrayPosisi = new ArrayList<>();

    private long lastUpdate = 0;
    private double last_x, last_y, last_z;
    double speed1, last_speed, last_position, last_time;
    private static final int SHAKE_THRESHOLD = 600;
    private static final int REQUEST_CODE_STORAGE = 100;

    double[] percepatan = new double[10000];
    double[] kecepatan = new double[10000];
    double[] posisi = new double[10000];
    double[] waktu = new double[10000];
    double[] selisihWaktu = new double[10000];
    int index = 0;

    Date timelast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // initialization
        graphPercepatan = (GraphView) findViewById(R.id.graph_percepatan);
        graphKecepatan = (GraphView) findViewById(R.id.graph_kecepatan);
        graphPosisi = (GraphView) findViewById(R.id.graph_posisi);
        tvJumlah = (TextView) findViewById(R.id.jumlah);
        btnExport = (Button) findViewById(R.id.btn_export);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_FASTEST);

        timelast =  new Date(System.currentTimeMillis());

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23) {
                    if (periksaIzinPenyimpanan()) {
                        export();
                    }
                } else {
                    export();
                }
            }
        });

        initGraphPercepatan(graphPercepatan);
        initGraphKecepatan(graphKecepatan);
        initGraphPosisi(graphPosisi);
    }


    public boolean periksaIzinPenyimpanan() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)  == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
                return false;
            }
        } else {
            return true;
        }
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
        graph.getViewport().setMinY(-10);
        // maximal y
        graph.getViewport().setMaxY(10);

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
git p        graph.addSeries(mSeriesPosisi);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            long curTime = System.currentTimeMillis();
            long diffTime = (curTime - lastUpdate);



            Date timeNow = new Date(System.currentTimeMillis());
            double timeDelta = timeNow.getTime() - timelast.getTime();
            double waktuSekarang = timeDelta/1000;



            double deltaT = waktuSekarang - last_time;
            double y = event.values[1] - GRAVITY;
            double speed1 = last_speed + (((y  + last_y) / 2) * deltaT);
            double position1 = last_position + (((speed1 + last_speed) / 2) * deltaT);
//            float position = ((speed1 + last_speed) / 2 * diffTime) +  last_position;

            if ((curTime - lastUpdate) > 10) {
                lastUpdate = curTime;

                //float speed = Math.abs(y  - last_y)/ diffTime * 10000;

                waktu[index] = waktuSekarang;
                posisi[index] = position1;
                percepatan[index] = y;
                kecepatan[index] = speed1;
                selisihWaktu[index] = deltaT;


                //float posisi = Math.abs(x + y + z - last_x)/ diffTime * 10000;


                last_y = y;
                last_speed = speed1;
                last_position = position1;
                last_time = waktuSekarang;



                double valuePercepatan = percepatan[index];
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

                double valueKecepatan = kecepatan[index];
                graphYValueKecepatan = valueKecepatan;
                // menambahkan data  ke dalam array untuk nanti disimpan ke database
                arrayKecepatan.add(graphYValueKecepatan);
                // bagian ini untuk menambah titik pada grafik
                mSeriesKecepatan.appendData(new DataPoint(graphLastXValueKecepatan, graphYValueKecepatan), true, 1000);
                // bagian ini untuk mengatur penambahan nilai x
                graphLastXValueKecepatan += 10;

                double valuePosisi = posisi[index];
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
       data.append("Percepatan,Kecepatan,Posisi,Waktu,Delta T");

//       for(int i = 0; i < 500;i++){
//           data.append("\n"+percepatan[i]+","+kecepatan[i]+","+posisi[i]+","+waktu[i]+","+selisihWaktu[i]);
//       }

        for(int i = 0; i < arrayKecepatan.size(); i++){
            data.append("\n"+arrayPercepatan.get(i)+","+arrayKecepatan.get(i)+","+arrayPosisi.get(i)+","+waktu[i]+","+selisihWaktu[i]);
        }

        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return;
        }

        String path = Environment.getExternalStorageDirectory().toString() + "/kominfo.proyek1";
        File parent = new File(path);
        if (parent.exists()) {
            try {
                FileOutputStream fos = openFileOutput("data.csv",Context.MODE_PRIVATE);
                fos.write(data.toString().getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            parent.mkdir();
            try {
                FileOutputStream fos = openFileOutput("data.csv",Context.MODE_PRIVATE);
                fos.write(data.toString().getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        try{
//            FileOutputStream fos = openFileOutput("data.csv",Context.MODE_PRIVATE);
//            fos.write(data.toString().getBytes());
//            fos.close();
//
//            Context context = getApplicationContext();
//            File filelocation = new File(getFilesDir(), "data.csv");
//            Uri path = FileProvider.getUriForFile(context, "com.solvedev.accelerometertest.fileprovider",filelocation);
//            Intent fileIntent = new Intent(Intent.ACTION_SEND);
//            fileIntent.setType("text/csv");
//            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
//            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
//            startActivity(Intent.createChooser(fileIntent, "Send Email"));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
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
