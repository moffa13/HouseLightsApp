package houselights.moffa.com.houselightsapp;

import java.io.Serializable;

/**
 * Created by moffa on 23-06-18.
 */

public class IOTDevice implements Serializable{

    private String _ip;
    private String _name;

    protected IOTDevice(IOTDevice iotDevice){
        _ip = iotDevice.getIP();
        _name = iotDevice.getName();
    }

    public IOTDevice(String ip, String name){
        _ip = ip;
        _name = name;
    }

    @Override
    public boolean equals(Object o){
        if(o == this) return true;
        if(!(o instanceof IOTDevice)) return false;
        IOTDevice iot = (IOTDevice)o;
        if(iot.getIP().equals(getIP()) && iot.getName().equals(getName())) return true;
        return false;
    }

    public String getName(){
        return _name;
    }

    public String getIP(){
        return _ip;
    }

    public void setName(String name) {
        this._name = name;
    }

    public void setIp(String ip) {
        this._ip = ip;
    }
}
