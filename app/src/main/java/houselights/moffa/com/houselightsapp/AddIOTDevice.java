package houselights.moffa.com.houselightsapp;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import org.w3c.dom.Text;

public class AddIOTDevice extends AppCompatActivity {

    private EditText _editName;
    private EditText _editIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_iotdevice);

        _editName = (EditText)findViewById(R.id.deviceName);
        _editIP = (EditText)findViewById(R.id.ipAddress);
    }

    public void addDevice(View v){
        if(TextUtils.isEmpty(_editName.getText()) || TextUtils.isEmpty(_editIP.getText())){
            View mdr = findViewById(R.id.constraint);
            Snackbar.make(mdr, "Un des elements n'est pas rempli", Snackbar.LENGTH_LONG).show();
            return;
        }

        IOTDevice iot = new IOTDevice(_editIP.getText().toString(), _editName.getText().toString());

        Intent i = new Intent();
        Bundle b = new Bundle();
        b.putSerializable("elem", iot);
        i.putExtras(b);
        setResult(RESULT_OK, i);
        finish();

    }
}
