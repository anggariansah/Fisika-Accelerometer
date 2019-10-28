package com.solvedev.accelerometertest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    TextView tvAngka, tvKecepatan, tvPosisi;

    float[] percepatan = new float[1000];
    float[] kecepatan = new float[1000];
    float[] posisi = new float[1000];
    float[] waktu = new float[1000];
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        tvAngka = (TextView)findViewById(R.id.angka);
        tvKecepatan = (TextView)findViewById(R.id.kecepatan);
        tvPosisi = (TextView)findViewById(R.id.posisi);
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



                tvAngka.setText("Percepatan = "+percepatan[index]);
                tvKecepatan.setText("Kecepatan = "+kecepatan[index]);
                tvPosisi.setText("Posisi = "+posisi[index]);

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
