package org.iot.dsa.dslink.system;

import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSMetadata;
import org.iot.dsa.node.DSValueType;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

public class Util {

    private static final double SPACE_KB = 1024;
    private static final double SPACE_MB = 1024 * SPACE_KB;
    private static final double SPACE_GB = 1024 * SPACE_MB;
    private static final double SPACE_TB = 1024 * SPACE_GB;
    private static final int PATH_COUNT = 2;

    public static String formatBytes(long sizeInBytes, String max ) {
        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(2);
        if(max==null){
            max="";
        }

        try {
            if ( sizeInBytes < SPACE_KB || max.equalsIgnoreCase("Byte")) {
                return nf.format(sizeInBytes) + " Byte(s)";
            } else if ( sizeInBytes < SPACE_MB || max.equalsIgnoreCase("KB") ) {
                return nf.format(sizeInBytes/SPACE_KB) + " KB";
            } else if ( sizeInBytes < SPACE_GB || max.equalsIgnoreCase("MB") ) {
                return nf.format(sizeInBytes/SPACE_MB) + " MB";
            } else if ( sizeInBytes < SPACE_TB  || max.equalsIgnoreCase("GB")) {
                return nf.format(sizeInBytes/SPACE_GB) + " GB";
            } else {
                return nf.format(sizeInBytes/SPACE_TB) + " TB";
            }
        } catch (Exception e) {
            return sizeInBytes + " Byte(s)";
        }
    }

    public static String round(double d, int fraction){
        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(fraction);
        return nf.format(d);
    }

    public static String round(float d, int fraction){
        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(fraction);
        return nf.format(d);
    }

    public static String round(long d, int fraction){
        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(fraction);
        return nf.format(d);
    }

    public static JSONObject calculatePID() {
        JSONObject response= new JSONObject();
        SystemInfo si = new SystemInfo();

        OperatingSystem os = si.getOperatingSystem();

        String pidList  = readFile();
        String[] strArr = getPidList(pidList);

        List<OSProcess> procs = Arrays.asList(os.getProcesses(os.getProcessCount(), OperatingSystem.ProcessSort.CPU));

        for (int i = 0; i < procs.size() && i < os.getProcessCount(); i++) {
            OSProcess p = procs.get(i);
            if(p.getName().equalsIgnoreCase("dart")) {
                for (int index = 0; index < strArr.length; index = index + 2) {
                    String pid = strArr[index].substring(1, strArr[index].length()-1);
                    if (String.valueOf(p.getProcessID()).equalsIgnoreCase(pid)) {
                        String dfName = strArr[index+1].substring(1, strArr[index+1].length()-1);
                        response.put(pid, getDFDetails(dfName, "sample", FormatUtil.formatBytes(p.getResidentSetSize()), String.valueOf(p.getOpenFiles())));
                    }
                }
            }
        }
        return response;
    }

    public static JSONObject getDFDetails(String linkName, String cmd, String memUsg, String opnFl){
        JSONObject dslink = new JSONObject();
        dslink.put("LinkName", linkName);
        dslink.put("Command", cmd);
        dslink.put("MemoryUsage", memUsg);
        dslink.put("OpenFile", opnFl);
        return dslink ;
    }

    public static String[] getPidList(String pidList) {

        if(pidList == null || pidList.isEmpty())
            return null;

        pidList = pidList.replace("{", "");
        pidList = pidList.replace("}", "");
        pidList = pidList.replace(":", ",");

        return pidList.split(",");
    }

    public static String readFile() {
        int count = 0;
        String fPath = null;
        try {
            fPath = new File(".").getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(count < PATH_COUNT) {
            Path path = Paths.get(fPath);
            fPath = path.getParent().toString();
            count++;
        }

        fPath = fPath.concat("/.pids");

        File file = new File(fPath);

        StringBuffer buffString = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                buffString.append(st);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffString.toString();
    }

    public static DSMap makeColumn(String name, DSValueType type) {
        return new DSMetadata().setName(name).setType(type).getMap();
    }

}
