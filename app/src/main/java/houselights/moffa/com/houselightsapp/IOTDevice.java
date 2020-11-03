package houselights.moffa.com.houselightsapp;

import android.os.StrictMode;
import android.util.Patterns;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

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

    public String getRealIP(){
        String port = "";
        String ipWithoutPort = "";

        String[] ipSplit = _ip.split(":");
        if(ipSplit.length == 2){
            port = ipSplit[1];
            ipWithoutPort = ipSplit[0];
        }else{
            ipWithoutPort = _ip;
        }

        if(Patterns.IP_ADDRESS.matcher(ipWithoutPort).matches()){ // Ip entered
            return _ip;
        }else {
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                InetAddress inetAddr = InetAddress.getByName(ipWithoutPort);

                byte[] addr = inetAddr.getAddress();
                String ipAddr = "";
                for (int i = 0; i < addr.length; i++) {
                    if (i > 0) {
                        ipAddr += ".";
                    }
                    ipAddr += addr[i] & 0xFF;
                }
                return ipAddr + ":" + port;
            } catch (UnknownHostException e) {
                return null;
            }
        }
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
