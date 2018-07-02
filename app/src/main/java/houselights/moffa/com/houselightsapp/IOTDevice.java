package houselights.moffa.com.houselightsapp;

import java.io.Serializable;

/**
 * Created by moffa on 23-06-18.
 */

public class IOTDevice implements Serializable{

    private final String _ip;
    private final String _name;

    protected IOTDevice(IOTDevice iotDevice){
        _ip = iotDevice.getIP();
        _name = iotDevice.getName();
    }

    public IOTDevice(String ip, String name){
        _ip = ip;
        _name = name;
    }

    public String getName(){
        return _name;
    }

    public String getIP(){
        return _ip;
    }
}
