package houselights.moffa.com.houselightsapp;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class IOTDeviceParamsActivity extends AppCompatActivity {

    private IOTDevice _device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iotdevice_params);

        _device = (IOTDevice)getIntent().getExtras().getSerializable("device");
        if(_device == null)
            finish();

        CallbackHandler<String> handler = new CallbackHandler<>();
        handler.registerCallback(new Callback() {
            @Override
            public void run(final ActionInterface ai) {
                getParam("sunset_warn", new APIActionInterface<String>() {
                    @Override
                    public void action(boolean error, String response) {
                        if(!error){
                            NumberPicker picker = findViewById(R.id.param_sunset_warn_value);
                            Spinner timeTypeSpinner = findViewById(R.id.param_sunset_warn_type);
                            Spinner beforeAfterSpinner = findViewById(R.id.param_sunset_before_after);
                            int value = 0;
                            if(response.indexOf("-") != -1){
                                beforeAfterSpinner.setSelection(1); // After because number is negative
                                value = Integer.parseInt(response.substring(1));
                            }else{
                                beforeAfterSpinner.setSelection(0);
                                value = Integer.parseInt(response);
                            }
                            adjustNumberPickerWithSpinner(picker, timeTypeSpinner, value);

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
                    public void action(boolean error, String response) {
                        if(!error){
                            NumberPicker picker = findViewById(R.id.param_power_min_value);
                            Spinner timeTypeSpinner = findViewById(R.id.param_power_min_type);
                            adjustNumberPickerWithSpinner(picker, timeTypeSpinner, Integer.parseInt(response));
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
                            NumberPicker picker = findViewById(R.id.param_power_max_value);
                            Spinner timeTypeSpinner = findViewById(R.id.param_power_max_type);
                            adjustNumberPickerWithSpinner(picker, timeTypeSpinner, Integer.parseInt(response));
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
                    Log.d("LOLL", "ERROR");
            }
        });


        
    }

    private static void setPickerMaxLimits(NumberPicker picker, int selection){
        if(selection == 0){ // seconds
            picker.setMinValue(0);
            picker.setMaxValue(86400);
        }else if (selection == 1){
            picker.setMinValue(1);
            picker.setMaxValue(59);
        }else if (selection == 2){
            picker.setMinValue(1);
            picker.setMaxValue(24);
        }
    }

    private static void adjustNumberPickerWithSpinner(NumberPicker picker, Spinner spinner, int seconds){
        if(!isMultipleOfTimeValue(seconds) || seconds < 60){
            setPickerMaxLimits(picker, 0);
            picker.setValue(seconds);
            spinner.setSelection(0);
            return;
        }

        if(seconds < 3600){ // 1min - 59min
            setPickerMaxLimits(picker, 1);
            picker.setValue(seconds / 60);
            spinner.setSelection(1);
        }else{ // 1hour - 24 hour
            setPickerMaxLimits(picker, 2);
            picker.setMinValue(1);
            picker.setMaxValue(24);
            picker.setValue(seconds / 3600);
            spinner.setSelection(2);
        }
    }

    private static boolean isMultipleOfTimeValue(int seconds){
        return seconds % 3600 == 0;
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
                finish();
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("param_name", String.valueOf(paramName));
                return params;
            }
        };
        queue.add(request);
    }
}
