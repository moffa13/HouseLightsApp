package houselights.moffa.com.houselightsapp;

import android.widget.ImageView;
import android.widget.Spinner;

/**
 * Created by moffa on 02-07-18.
 */

public class IOTDeviceWithGraphics extends IOTDevice {

    private Spinner _spinnerView;
    private ImageView _connectedStateView;
    private ImageView _realStateView;
    private ImageView _deleteCrossView;

    public IOTDeviceWithGraphics(IOTDevice iotDevice){
        super(iotDevice);
    }

    public void setSpinnerView(Spinner spinner){
        _spinnerView = spinner;
    }

    public void setConnectedStateView(ImageView image){
        _connectedStateView = image;
    }

    public void setRealStateView(ImageView image){
        _realStateView = image;
    }

    public void setDeleteCrossView(ImageView image){
        _deleteCrossView = image;
    }

    public Spinner getSpinnerView(){
        return _spinnerView;
    }

    public ImageView getConnectedStateView(){
        return _connectedStateView;
    }

    public ImageView getRealStateView(){
        return _realStateView;
    }

    public ImageView getDeleteCrossView(){
        return _deleteCrossView;
    }


}
