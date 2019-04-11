package houselights.moffa.com.houselightsapp;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class IOTDeviceParamsActivity extends AppCompatActivity {

    private IOTDevice _device;

    EditText deviceName;
    EditText deviceIp;

    NumberPicker paramSunsetWarnValueMain;
    NumberPicker paramSunsetWarnValueSub;
    NumberPicker paramSunsetWarnValueSubSub;
    Spinner paramSunsetWarnBeforeAfter;

    NumberPicker paramPowerMinValueMain;
    NumberPicker paramPowerMinValueSub;
    NumberPicker paramPowerMinValueSubSub;

    NumberPicker paramPowerMaxValueMain;
    NumberPicker paramPowerMaxValueSub;
    NumberPicker paramPowerMaxValueSubSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iotdevice_params);

        _device = (IOTDevice)getIntent().getExtras().getSerializable("device");
        if(_device == null)
            finish();

        deviceName = findViewById(R.id.deviceName);
        deviceIp = findViewById(R.id.deviceIp);

        paramSunsetWarnValueMain = findViewById(R.id.param_sunset_warn_value_main);
        paramSunsetWarnValueSub = findViewById(R.id.param_sunset_warn_value_sub);
        paramSunsetWarnValueSubSub = findViewById(R.id.param_sunset_warn_value_sub_sub);
        paramSunsetWarnBeforeAfter = findViewById(R.id.param_sunset_before_after);

        paramPowerMinValueMain = findViewById(R.id.param_power_min_value_main);
        paramPowerMinValueSub = findViewById(R.id.param_power_min_value_sub);
        paramPowerMinValueSubSub = findViewById(R.id.param_power_min_value_sub_sub);

        paramPowerMaxValueMain = findViewById(R.id.param_power_max_value_main);
        paramPowerMaxValueSub = findViewById(R.id.param_power_max_value_sub);
        paramPowerMaxValueSubSub = findViewById(R.id.param_power_max_value_sub_sub);

        Log.d("LOL", String.valueOf(paramSunsetWarnBeforeAfter.getSelectedItemPosition()));

        registerUpdate("sunset_warn", paramSunsetWarnValueMain, paramSunsetWarnValueMain, paramSunsetWarnValueSub, paramSunsetWarnValueSubSub);
        registerUpdate("sunset_warn", paramSunsetWarnValueSub, paramSunsetWarnValueMain, paramSunsetWarnValueSub, paramSunsetWarnValueSubSub);
        registerUpdate("sunset_warn", paramSunsetWarnValueSubSub, paramSunsetWarnValueMain, paramSunsetWarnValueSub, paramSunsetWarnValueSubSub);
        registerUpdate("sunset_warn", paramSunsetWarnBeforeAfter, paramSunsetWarnValueMain, paramSunsetWarnValueSub, paramSunsetWarnValueSubSub);

        registerUpdate("power_min", paramPowerMinValueMain, paramPowerMinValueMain, paramPowerMinValueSub, paramPowerMinValueSubSub);
        registerUpdate("power_min", paramPowerMinValueSub, paramPowerMinValueMain, paramPowerMinValueSub, paramPowerMinValueSubSub);
        registerUpdate("power_min", paramPowerMinValueSubSub, paramPowerMinValueMain, paramPowerMinValueSub, paramPowerMinValueSubSub);

        registerUpdate("power_max", paramPowerMaxValueMain, paramPowerMaxValueMain, paramPowerMaxValueSub, paramPowerMaxValueSubSub);
        registerUpdate("power_max", paramPowerMaxValueSub, paramPowerMaxValueMain, paramPowerMaxValueSub, paramPowerMaxValueSubSub);
        registerUpdate("power_max", paramPowerMaxValueSubSub, paramPowerMaxValueMain, paramPowerMaxValueSub, paramPowerMaxValueSubSub);

        deviceName.setText(_device.getName());
        deviceIp.setText(_device.getIP());

        CallbackHandler<String> handler = new CallbackHandler<>();
        handler.registerCallback(new Callback() {
            @Override
            public void run(final ActionInterface ai) {
                getParam("sunset_warn", new APIActionInterface<String>() {
                    @Override
                    public void action(boolean error, final String response) {

                        if(!error){
                            int value = 0;
                            if(response.indexOf("-") != -1){
                                paramSunsetWarnBeforeAfter.setSelection(1); // After because number is negative
                                value = Integer.parseInt(response.substring(1));
                            }else{
                                paramSunsetWarnBeforeAfter.setSelection(0);
                                value = Integer.parseInt(response);
                            }
                            adjustNumberPickerWithSpinner(paramSunsetWarnValueMain, paramSunsetWarnValueSub, paramSunsetWarnValueSubSub, value);
                        }
                        ai.action(error);
                    }
                });
            }
        }).registerCallback(new Callback() {
            @Override
            public void run(final ActionInterface ai) {
                getParam("power_min", new APIActionInterface<String>() {
                    @Override
                    public void action(boolean error, final String response) {
                        if(!error){

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adjustNumberPickerWithSpinner(paramPowerMinValueMain, paramPowerMinValueSub, paramPowerMinValueSubSub, Integer.parseInt(response));
                                }
                            });
                        }
                        ai.action(error);
                    }
                });
            }
        }).registerCallback(new Callback() {
            @Override
            public void run(final ActionInterface ai) {
                getParam("power_max", new APIActionInterface<String>() {
                    @Override
                    public void action(boolean error, String response) {
                        if(!error){
                            adjustNumberPickerWithSpinner(paramPowerMaxValueMain, paramPowerMaxValueSub, paramPowerMaxValueSubSub, Integer.parseInt(response));
                        }
                        ai.action(error);
                    }
                });
            }
        }).registerCallback(new Callback() {
            @Override
            public void run(final ActionInterface ai) {
                getParam("google_api_key", new APIActionInterface<String>() {
                    @Override
                    public void action(boolean error, String response) {
                        if(!error){
                            EditText edit = findViewById(R.id.param_google_api_key);
                            edit.setText(response);
                        }
                        ai.action(error);
                    }
                });
            }
        }).run(new ActionInterface() {
            @Override
            public void action(boolean error) {
                if(!error)
                    Log.d("LOLL", "It worked !");
                else
                    Toast.makeText(getApplicationContext(), "Error retrieving parameters", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private static void setPickerMaxLimits(NumberPicker picker, TimeType selection){

        if(selection == TimeType.SECONDS || selection == TimeType.MINUTES){ // seconds
            picker.setMinValue(0);
            picker.setMaxValue(59);
        }else if (selection == TimeType.HOURS){
            picker.setMinValue(0);
            picker.setMaxValue(23);
        }
    }

    private void registerUpdate(final String param, NumberPicker toRegister, final NumberPicker hours, final NumberPicker minutes, final NumberPicker seconds){
        toRegister.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int secondsInt = hours.getValue() * 3600 + minutes.getValue() * 60 + seconds.getValue();
                setParam(param, secondsInt);
            }
        });
    }

    private void registerUpdate(final String param, Spinner toRegister, final NumberPicker hours, final NumberPicker minutes, final NumberPicker seconds){
        toRegister.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                int secondsInt = hours.getValue() * 3600 + minutes.getValue() * 60 + seconds.getValue();
                setParam(param, secondsInt);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }


    private void setParam(String name, int value){
        if(name.equalsIgnoreCase("sunset_warn") && paramSunsetWarnBeforeAfter.getSelectedItemPosition() == 1)
            value = -value;
        setParam(name, String.valueOf(value), new APIActionInterface<String>() {
            @Override
            public void action(boolean error, String s) {
                if(error){
                    Toast.makeText(getApplicationContext(), "Error settings parameters", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void adjustNumberPickerWithSpinner(final NumberPicker main, final NumberPicker sub, final NumberPicker subSub, final int seconds){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                setPickerMaxLimits(main, TimeType.HOURS);
                setPickerMaxLimits(sub, TimeType.MINUTES);
                setPickerMaxLimits(subSub, TimeType.SECONDS);

                if(seconds < 60){
                    subSub.setValue(seconds);
                }else if(seconds < 3600){
                    sub.setValue(seconds / 60);
                    subSub.setValue(seconds % 60);
                }else{
                    main.setValue(seconds / 3600);
                    int v = seconds - (3600 * (seconds / 3600));
                    sub.setValue(v / 60);
                    subSub.setValue(v % 60);
                }
            }
        });
    }

    private void setParam(final String paramName, final String paramValue, final APIActionInterface<String> actionInterface){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "http://" + _device.getIP() + "/param/set", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                actionInterface.action(false, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                actionInterface.action(true, null);
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("param_name", paramName);
                params.put("param_value", paramValue);
                return params;
            }
        };
        queue.add(request);
    }

    private void getParam(final String paramName, final APIActionInterface<String> actionInterface){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "http://" + _device.getIP() + "/param/get", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                actionInterface.action(false, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                actionInterface.action(true, null);
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("param_name", paramName);
                return params;
            }
        };
        queue.add(request);
    }
}
