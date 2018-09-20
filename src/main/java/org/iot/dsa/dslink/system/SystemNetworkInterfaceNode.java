package org.iot.dsa.dslink.system;

import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class SystemNetworkInterfaceNode extends DSNode {

    public SystemNetworkInterfaceNode() {

    }

    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        addNetworkInterfaces();
    }

    private void addNetworkInterfaces(){
        try {
            Enumeration<NetworkInterface> netIntfs = NetworkInterface.getNetworkInterfaces();
            while(netIntfs.hasMoreElements() ){
                NetworkInterface netf = netIntfs.nextElement();
                Enumeration<InetAddress> inetas = netf.getInetAddresses();
                String ips = "" ;
                while(inetas.hasMoreElements() ){
                    InetAddress ineta = inetas.nextElement();
                    String address = ineta.getHostAddress().replaceAll("%"+netf.getName(),"");
                    ips = ips + (ips.equals("")?"":",") + address;
                }
                if(!ips.equals("")){
                    put(netf.getDisplayName(),DSString.valueOf(ips)).setReadOnly(true);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
