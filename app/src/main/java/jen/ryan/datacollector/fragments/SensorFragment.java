package jen.ryan.datacollector.fragments;

/**
 * Created by Chris on 4/21/15.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import jen.ryan.datacollector.R;
import jen.ryan.datacollector.sensors.SensorHelper;
import jen.ryan.datacollector.sensors.ValuesCallback;

/**
 * A placeholder fragment containing a simple view.
 */
public class SensorFragment extends Fragment {
    Context context;
    TextView console;
    StringBuilder sb;
    boolean isRunning = true;
    String value;

    public SensorFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sb = new StringBuilder();
        SensorHelper.getInstance(context).setCallback(new ValuesCallback() {
            @Override
            public void handleValues(float[] values) {
                if (sb != null) {
                    value = Arrays.toString(values);
                    sb.append("\n").append(value.substring(1, value.length() - 1));
                }
                if (console != null) {
                    console.setText(sb.toString());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        console = (TextView) rootView.findViewById(R.id.logger);
        console.setScroller(new Scroller(context, new LinearInterpolator()));
        rootView.findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning ^= true;
                ((TextView) view).setText(isRunning ? "Pause" : "Start");
                if (isRunning)
                    SensorHelper.getInstance(context).onResume();
                else
                    SensorHelper.getInstance(context).onPause();
            }
        });

        rootView.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, Void>() {
                    File file= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getDate(System.currentTimeMillis()) + ".csv");

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            FileOutputStream outputStream = new FileOutputStream(file);
//                            Log.i("DebugDebug", outputStream.getFD().toString());
                            outputStream.write(sb.toString().getBytes());
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Toast.makeText(context, "Saved: " + file.toString(), Toast.LENGTH_LONG).show();
                    }
                }.execute();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SensorHelper.getInstance(context).onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        SensorHelper.getInstance(context).onPause();
    }

    public static String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("MM_dd_hh_mm_ss_SSS");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}