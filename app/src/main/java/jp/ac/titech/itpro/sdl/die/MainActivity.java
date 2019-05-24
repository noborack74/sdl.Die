package jp.ac.titech.itpro.sdl.die;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, SensorEventListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private GLSurfaceView glView;
    private SimpleRenderer renderer;

    private Cube cube;
    private Pyramid pyramid;
    private Octahedron octahedron;

    private SensorManager manager;
    private Sensor gyroscope;
    private static final double NS2S = 1.0f / 1000000000.0f;
    private double timestamp;
    private double radX = 0;
    private double radY = 0;
    private double radZ = 0;

    private SeekBar seekBarX;
    private SeekBar seekBarY;
    private SeekBar seekBarZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);


        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (manager == null) {
            Toast.makeText(this, R.string.toast_no_sensor_manager, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        gyroscope = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscope == null) {
            Toast.makeText(this, R.string.toast_no_gyroscope, Toast.LENGTH_LONG).show();
        }



        glView = findViewById(R.id.gl_view);
        seekBarX = findViewById(R.id.seekbar_x);
        seekBarY = findViewById(R.id.seekbar_y);
        seekBarZ = findViewById(R.id.seekbar_z);
        seekBarX.setMax(360);
        seekBarY.setMax(360);
        seekBarZ.setMax(360);
        seekBarX.setOnSeekBarChangeListener(this);
        seekBarY.setOnSeekBarChangeListener(this);
        seekBarZ.setOnSeekBarChangeListener(this);

        renderer = new SimpleRenderer();
        cube = new Cube();
        pyramid = new Pyramid();
        octahedron = new Octahedron();
        //renderer.setObj(cube);
        renderer.setObj(octahedron);
        glView.setRenderer(renderer);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        manager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        glView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        manager.unregisterListener(this);
        glView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
        case R.id.menu_cube:
            renderer.setObj(cube);
            break;
        case R.id.menu_pyramid:
            renderer.setObj(pyramid);
            break;
        case R.id.menu_octahedron:
            renderer.setObj(octahedron);
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
        case R.id.seekbar_x:
            renderer.rotateObjX(progress);
            break;
        case R.id.seekbar_y:
            renderer.rotateObjY(progress);
            break;
        case R.id.seekbar_z:
            renderer.rotateObjZ(progress);
            break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        float omegaX = event.values[0];
        float omegaY = event.values[1];
        float omegaZ = event.values[2];  // z-axis angular velocity (rad/sec)
        // TODO: calculate right direction that cancels the rotation


        if (timestamp != 0) {
            final double dT = (event.timestamp - timestamp) * NS2S;
            radX += omegaX * dT;
            radY += omegaY * dT;
            radZ += omegaZ * dT;


            seekBarX.setProgress((int)Math.ceil(radX % (Math.PI * 2) * 360));
            seekBarY.setProgress((int)Math.ceil(radY % (Math.PI * 2) * 360));
            seekBarZ.setProgress((int)Math.ceil(radZ % (Math.PI * 2) * 360));

        }

        timestamp = event.timestamp;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged: accuracy=" + accuracy);
    }
}
