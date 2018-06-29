package houselights.moffa.com.houselightsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int LIGHT_STATE_UPDATE_INTERVAL = 10000;
    private final Handler _handler;
    private ArrayList<IOTDevice> _iots;
    private ArrayList<String> _powerValues;
    private LinearLayout _iotsLayout;
    private BroadcastReceiver _connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkIOTConnectivity();
        }
    };

    public MainActivity(){
        _handler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _iots = new ArrayList<>();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _iotsLayout = findViewById(R.id.iots_layout);
        _powerValues = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.power_modes_array)));

        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(_connectionReceiver, filter);

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

    private void checkIOTConnectivity(){
        for(int i = 0; i < _iots.size(); ++i){
            IOTDevice device = _iots.get(i);
            LinearLayout itemParent = (LinearLayout)_iotsLayout.getChildAt(i);
            Spinner spinner = (Spinner)itemParent.findViewById(R.id.power_mode_select);
            ImageView connectedState = (ImageView)itemParent.findViewById(R.id.iot_connected_icon_view);
            setConnectedState(device, spinner, connectedState, null);
        }
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
                    loadIOTToLayout(iot, _iots.size() - 1);
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
        int i = 0;
        for(final IOTDevice iot : _iots){
            loadIOTToLayout(iot, i);
            i++;
        }
    }

    private void loadIOTToLayout(final IOTDevice iot, int id){

        LayoutInflater inflater = getLayoutInflater();
        View iot_details_template = inflater.inflate(R.layout.iot_list_item, null);
        TextView name = (TextView)iot_details_template.findViewById(R.id.iot_name);
        name.setText(iot.getName());

        final Spinner spinner = iot_details_template.findViewById(R.id.power_mode_select);
        final ImageView connectedIcon = iot_details_template.findViewById(R.id.iot_connected_icon_view);
        final ImageView deleteIcon = iot_details_template.findViewById(R.id.iot_delete_item);
        final ImageView bulbIcon = iot_details_template.findViewById(R.id.iot_real_state_icon_view);
        deleteIcon.setTag(new Integer(id));
        spinner.setEnabled(false);

        final Runnable rn = new Runnable() {
            @Override
            public void run() {
                final Runnable rrr = this;
                setConnectedState(iot, spinner, connectedIcon, new ActionInterface() {
                    @Override
                    public void action(boolean error) {
                        if(!error)
                            checkRealState(iot, bulbIcon, new ActionInterface() {
                                @Override
                                public void action(boolean error_real_state) {
                                    _handler.postDelayed(rrr, LIGHT_STATE_UPDATE_INTERVAL);
                                }
                            });
                        else
                            _handler.postDelayed(rrr, LIGHT_STATE_UPDATE_INTERVAL);
                    }
                });

            }
        };
        rn.run();

        spinner.setOnItemSelectedListener(new SpinnerListener(new OnSelectedItemInterface() {
            @Override
            public void selected(int value, boolean firstSelection) {
                if(!firstSelection)
                    request(iot.getIP(), getIdFromText(_powerValues.get(value)), connectedIcon, bulbIcon);
            }
        }));

        _iotsLayout.addView(iot_details_template);
    }

    private void setConnectedState(IOTDevice device, final Spinner spinner, final ImageView connectedIcon, final ActionInterface ii){
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(new StringRequest(Request.Method.GET, "http://" + device.getIP() + "/get", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int newValue = _powerValues.indexOf(getTextfromId(Integer.parseInt(response)));
                spinner.setEnabled(true);
                spinner.setSelection(newValue);
                connectedIcon.setImageResource(R.mipmap.iot_connected_icon);
                if(ii != null)
                    ii.action(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                connectedIcon.setImageResource(R.mipmap.iot_disconnected_icon);
                if(ii != null)
                    ii.action(true);
            }
        }));
    }

    public void deleteItem(final View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete_item);
        DialogInterface.OnClickListener listener =  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == AlertDialog.BUTTON_POSITIVE){
                    Integer id = (Integer)v.getTag();
                    _iots.remove(id.intValue());
                    ((ViewGroup)v.getParent().getParent()).removeView((ViewGroup)v.getParent());
                    addIOTTOPrefs();
                }
            }
        };
        builder.setPositiveButton(R.string.yes, listener);
        builder.setNegativeButton(R.string.no, listener);
        builder.show();

    }

    private static int getIdFromText(String val){
        if(val.equals("ON")) return 1;
        if(val.equals("OFF")) return 0;
        return -1;
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

    private void checkRealState(IOTDevice device, final ImageView icon, final ActionInterface ii){
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(new StringRequest(Request.Method.POST, "http://" + device.getIP() + "/get_real", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("1")){
                    icon.setImageResource(R.mipmap.iot_on_icon);
                }else{
                    icon.setImageResource(R.mipmap.iot_off_icon);
                }
                if(ii != null)
                    ii.action(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                icon.setImageResource(R.mipmap.iot_off_icon);
                if(ii != null)
                    ii.action(true);
            }
        }));
    }

    private void request(String ip, final int value, final ImageView icon, final ImageView bulbIcon){
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(new StringRequest(Request.Method.POST, "http://" + ip + "/set", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                icon.setImageResource(R.mipmap.iot_connected_icon);
                if(value == 1)
                    bulbIcon.setImageResource(R.mipmap.iot_on_icon);
                else if(value == 0)
                    bulbIcon.setImageResource(R.mipmap.iot_off_icon);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                icon.setImageResource(R.mipmap.iot_disconnected_icon);
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
