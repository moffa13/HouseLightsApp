package houselights.moffa.com.houselightsapp;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.NumberPicker;
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

        NumberPicker picker = findViewById(R.id.param_sunset_warn_value);
        picker.setMinValue(-86400);
        picker.setMaxValue(86400);


        CallbackHandler<String> handler = new CallbackHandler<>();
        handler.registerCallback(new Callback() {
            @Override
            public void run(final ActionInterface ai) {
                getParam("sunset_warn", new APIActionInterface<String>() {
                    @Override
                    public void action(boolean error, String response) {
                        if(!error){
                            NumberPicker picker = findViewById(R.id.param_sunset_warn_value);
                            picker.setValue(Integer.valueOf(response));
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
                            picker.setValue(Integer.valueOf(response));
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
