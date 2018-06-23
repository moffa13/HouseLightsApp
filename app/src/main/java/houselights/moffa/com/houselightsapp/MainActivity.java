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
                    loadIOTSToLayout();
                }
            }
        }
    }

    private void getIOTFromPrefs(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            _iots = (ArrayList<IOTDevice>)ObjectSerializer.deserialize(prefs.getString("iots", ObjectSerializer.serialize(new ArrayList<IOTDevice>())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadIOTSToLayout(){
        for(final IOTDevice iot : _iots){
            RequestQueue queue = Volley.newRequestQueue(this);
            final TextView stateView = new TextView(this);

            queue.add(new StringRequest(Request.Method.GET, "http://" + iot.getIP() + "/get", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    int val = Integer.parseInt(response);
                    switch (val){
                        case 0:
                            stateView.setText("OFF");
                            break;
                        case 1:
                            stateView.setText("ON");
                            break;
                        case -1:
                            stateView.setText("AUTO");
                            break;
                        default:

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    stateView.setText("ERROR");
                    Log.d("LOL", error.toString());
                }
            }));



            LinearLayout elem = new LinearLayout(this);
            elem.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            elem.setLayoutParams(params);
            TextView name = new TextView(this);
            name.setText(iot.getName());
            Button btnOn = new Button(this);
            btnOn.setText("ON");
            btnOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    request(iot.getIP(), 1);
                }
            });

            Button btnOff = new Button(this);
            btnOff.setText("OFF");
            btnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    request(iot.getIP(), 0);
                }
            });

            Button btnAuto = new Button(this);
            btnAuto.setText("AUTO");
            btnAuto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    request(iot.getIP(), -1);
                }
            });

            elem.addView(name);
            elem.addView(btnOn);
            elem.addView(btnOff);
            elem.addView(btnAuto);
            elem.addView(stateView);
            _iotsLayout.addView(elem);

        }
    }

    private void request(String ip, final int value){
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(new StringRequest(Request.Method.POST, "http://" + ip + "/set", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
