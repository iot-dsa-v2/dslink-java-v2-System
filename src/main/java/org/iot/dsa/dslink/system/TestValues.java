package org.iot.dsa.dslink.system;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import javax.management.MBeanServer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;

import com.sun.management.*;

import org.iot.dsa.io.json.JsonReader;
import org.iot.dsa.io.json.JsonWriter;

import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

public class TestValues {

    public static void main(String args[]){

        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        System.out.println("os "+ os.getName()  );
        MBeanServer plat = ManagementFactory.getPlatformMBeanServer();
        System.out.println("plat "+ plat.getDomains());

        printUsage();
        getmemvalues();
        System.out.println("\n\n");
        hostnet();
    }


    private static void testJson() {
        JsonWriter writer = new JsonWriter();
        //writer.
        writer.key("name");
        writer.value("John");

        JsonReader reader = new JsonReader();
        reader.getString();
    }

    private static void printUsage() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();

        for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().startsWith("get")
                    && Modifier.isPublic(method.getModifiers())) {
                Object value;
                try {
                    value = method.invoke(operatingSystemMXBean);
                } catch (Exception e) {
                    value = e;
                } // try
                System.out.println(method.getName() + " = " + value);
            } // if
        } // for
    }

    private static void getmemvalues() {
        SystemInfo si = new SystemInfo();
        //System.out.println("memory " +  si.getHardware().getMemory().getTotal());
        //System.out.println("family " +  si.getOperatingSystem().getFamily());

        NetworkIF[] networks = si.getHardware().getNetworkIFs();
        for(int i = 0 ; i < networks.length ; i ++){
            String ips = "";
            for (String net : networks[i].getIPv4addr()){
                ips = ips + (ips.equals("")?"":",") + net;
            }
            for (String net : networks[i].getIPv6addr()){
                ips = ips + (ips.equals("")?"":",") + net;
            }
            System.out.println(networks[i].getName() + " :  " + networks[i].getDisplayName() + " : " + ips );
            //System.out.println("interface " + networks[i].getNetworkInterface() );
        }

    }

    private static void hostnet(){
        try {
            InetAddress intA = java.net.InetAddress.getLocalHost();
            System.out.println("host=" + intA.getHostName());
            System.out.println("host=" + intA.getAddress());

            Enumeration<NetworkInterface> netIntfs = NetworkInterface.getNetworkInterfaces();
            while(netIntfs.hasMoreElements() ){
                NetworkInterface netf = netIntfs.nextElement();
                System.out.println( netf.getName() + " / " + netf.getDisplayName() + " / " + netf.getParent()+ " /loopback=" + netf.isLoopback() + " /pointToPoint=" + netf.isPointToPoint() + " /Virtual=" + netf.isVirtual() + " /Up=" + netf.isUp() );

                Enumeration<InetAddress> inetas = netf.getInetAddresses();
                if(inetas.hasMoreElements()){
                    //System.out.println( netf.getName() + " / " + netf.getDisplayName() + " / " + netf.getParent()+ " / " + netf.isLoopback() + " / " + netf.isPointToPoint() + " / " + netf.isVirtual() + " / " + netf.isUp() );
                }
                while(inetas.hasMoreElements() ){
                    InetAddress ineta = inetas.nextElement();
                    String address = ineta.getHostAddress().replaceAll("%"+netf.getName(),"");
                    System.out.println("\tineta1 " + address );
                    //System.out.println("\tineta2 " + ineta.getAddress() );
                    //System.out.println("\tineta3 " + ineta.getCanonicalHostName() );
                    //System.out.println("\tineta4 " + ineta.getHostName() );
                }

                /*
                ListIterator<InterfaceAddress> interfs = netf.getInterfaceAddresses().listIterator();
                while(interfs.hasNext()){
                    InterfaceAddress interf = interfs.next();
                    System.out.println("\tinterf " + interf.getAddress() );
                }
                Enumeration<NetworkInterface> subints = netf.getSubInterfaces();
                while(subints.hasMoreElements() ){
                    NetworkInterface subint = subints.nextElement();
                    System.out.println("\tsubint " + subint.getDisplayName() );
                }*/
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e){
            e.printStackTrace();
        }
    }

}
