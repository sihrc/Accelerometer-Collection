package jen.ryan.datacollector.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Chris on 4/21/15.
 */
public class SensorHelper {
    static SensorHelper instance;

    Context context;
    SensorManager manager;
    Sensor mAccelerometer;
    ValuesCallback callback;

    public static SensorHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SensorHelper();
            instance.context = context;
            instance.manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            instance.mAccelerometer =  instance.manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        }
        return instance;
    }

    public void setCallback(ValuesCallback callback) {
        this.callback = callback;
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_LINEAR_ACCELERATION)
                return;

            if (callback != null)
                callback.handleValues(event.values, event.timestamp);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public void onResume() {
        manager.registerListener(sensorEventListener, mAccelerometer, 35);
    }

    public void onPause() {
        manager.unregisterListener(sensorEventListener);
    }
}
