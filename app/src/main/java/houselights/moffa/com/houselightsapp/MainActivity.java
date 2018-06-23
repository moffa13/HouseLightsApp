package houselights.moffa.com.houselightsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ArrayList<IOTDevice> _iots;
    private LinearLayout _iotsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _iots = new ArrayList<>();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _iotsLayout = findViewById(R.id.iots_layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddIOTDevice.class);
                startActivityForResult(i, 1);
            }
        });

        getIOTFromPrefs();
        loadIOTSToLayout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i){
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                Serializable s = i.getSerializableExtra("elem");
                if(s != null){
                    IOTDevice iot = (IOTDevice)s;
                    _iots.add(iot);
                    addIOTTOPrefs();
                    loadIOTToLayout(iot);
                }
            }
        }
    }

    private void getIOTFromPrefs(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            String aaa = prefs.getString("iots", ObjectSerializer.serialize(new ArrayList<IOTDevice>()));
            _iots = (ArrayList<IOTDevice>)ObjectSerializer.deserialize(aaa);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadIOTSToLayout(){
        for(final IOTDevice iot : _iots){
            loadIOTToLayout(iot);
        }
    }

    private void loadIOTToLayout(final IOTDevice iot){
        RequestQueue queue = Volley.newRequestQueue(this);
        final TextView stateView = new TextView(this);

        queue.add(new StringRequest(Request.Method.GET, "http://" + iot.getIP() + "/get", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                stateView.setText(getTextfromId(Integer.parseInt(response)));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stateView.setText("ERROR");
            }
        }));

        LinearLayout elem = new LinearLayout(this);
        elem.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        elem.setLayoutParams(params);
        TextView name = new TextView(this);
        name.setText(iot.getName());
        final Button btnOn = new Button(this);
        final Button btnOff = new Button(this);
        final Button btnAuto = new Button(this);
        btnOn.setText("ON");
        btnOff.setText("OFF");
        btnAuto.setText("AUTO");

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request(iot.getIP(), 1, btnOn, btnOff, btnAuto, stateView);
            }
        });
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request(iot.getIP(), 0, btnOn, btnOff, btnAuto, stateView);
            }
        });
        btnAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request(iot.getIP(), -1, btnOn, btnOff, btnAuto, stateView);
            }
        });

        elem.addView(name);
        elem.addView(btnOn);
        elem.addView(btnOff);
        elem.addView(btnAuto);
        elem.addView(stateView);
        _iotsLayout.addView(elem);
    }

    private static String getTextfromId(int val){
        switch (val){
            case 0:
                return "OFF";
            case 1:
                return "ON";
            case -1:
                return "AUTO";
            default:
                return "ERROR";
        }
    }

    private void request(String ip, final int value, final Button btnOn, final Button btnOff, final Button btnAuto, final TextView stateView){
        btnOn.setEnabled(false);
        btnOff.setEnabled(false);
        btnAuto.setEnabled(false);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(new StringRequest(Request.Method.POST, "http://" + ip + "/set", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                btnOn.setEnabled(true);
                btnOff.setEnabled(true);
                btnAuto.setEnabled(true);
                stateView.setText(getTextfromId(value));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                btnOn.setEnabled(true);
                btnOff.setEnabled(true);
                btnAuto.setEnabled(true);
                stateView.setText("ERROR");
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("value", String.valueOf(value));
                return params;
            }
        });
    }

    private void addIOTTOPrefs(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();

        try {
            editor.putString("iots", ObjectSerializer.serialize(_iots));
        } catch (IOException e) {
            e.printStackTrace();
        }

        editor.commit();
    }
}
