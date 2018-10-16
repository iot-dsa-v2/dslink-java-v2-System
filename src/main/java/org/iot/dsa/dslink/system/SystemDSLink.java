package org.iot.dsa.dslink.system;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSMainNode;
import org.iot.dsa.node.DSBool;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSInt;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.ActionSpec;
import org.iot.dsa.node.action.ActionTable;
import org.iot.dsa.node.action.DSAction;
import org.iot.dsa.node.action.DSActionValues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PowerSource;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

/**
 * The main and only node of this link.
 *
 * @author Aaron Hansen
 */
public class SystemDSLink extends DSMainNode implements Runnable {

    ///////////////////////////////////////////////////////////////////////////
    // Instance Fields
    ///////////////////////////////////////////////////////////////////////////
    private DSRuntime.Timer timer;
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String MAC = "macOS";
    private static final String WINDOWS = "Window";
    private static final String LINUX = "Linux";

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    // Nodes must support the cc   public no-arg constructor.  Technically this isn't required
    // since there are no other constructors...
    public SystemDSLink() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Public Methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Called by the timer, increments the counter.
     */
    @Override
    public void run() {
        updateMetrics();
        DSInfo info = getInfo(SystemDSLinkConstants.PROCESS_NODE);
        if(info!=null) {
            //displayDiagnosticsModeProcess();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Protected Methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Defines the permanent children of this node type, their existence is guaranteed in all
     * instances.  This is only ever called once per, type per process.
     */
    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        systemActions();
        systemInot();
        updateMetrics();
        displayNetworkInterfaces();
    }

    /**
     * Cancels an active timer if there is one.
     */
    @Override
    protected void onStopped() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Starts the timer.
     */
    @Override
    protected void onSubscribed() {
        // Use DSRuntime for timers and its thread pool.
        //timer = DSRuntime.run(this, System.currentTimeMillis() + 1000, 1000);
        startTimer(1);
    }

    /**
     * Cancels the timer.
     */
    @Override
    protected void onUnsubscribed() {
        stopTimer();
        timer = null;
    }

    private void displayNetworkInterfaces() {
        put(SystemDSLinkConstants.NETWORK_INTERFACES, new SystemNetworkInterfaceNode());
    }

    private void displayDiagnosticsModeProcess() {
        put(SystemDSLinkConstants.PROCESS_NODE, new DiagnosticModeNode(1));
    }

    // Added by Ketan
    @Override
    protected void onInfoChanged(DSInfo info) {
        super.onInfoChanged(info);
    }

    @Override
    protected void onChildChanged(DSInfo info) {
        super.onChildChanged(info);
        if(info.getName().equalsIgnoreCase(SystemDSLinkConstants.POLL_RATE)) {
            stopTimer();
            startTimer(Integer.parseInt(info.getValue().toString()));
        }
        if(info.getName().equalsIgnoreCase(SystemDSLinkConstants.DIAGNOSTICS_MODE)) {
            if(info.getValue().toString().equalsIgnoreCase(FALSE)) {
                removeDiagnosticsModeNode();
            } else if (info.getValue().toString().equalsIgnoreCase(TRUE)) {
                displayDiagnosticsModeProcess();
            }
        }
    }

    private void systemActions() {
        declareDefault(SystemDSLinkConstants.EXECUTE_COMMAND, makeExecuteCommand());
        declareDefault(SystemDSLinkConstants.EXECUTE_COMMAND_STREAM, makeExecuteCommandStream());
        SystemInfo si = new SystemInfo();
        if(si.getOperatingSystem().getFamily().equalsIgnoreCase(MAC)) {
            declareDefault(SystemDSLinkConstants.RUN_APPLE_SCRIPT, makeRunAppleScript());
        } else if (si.getOperatingSystem().getFamily().equalsIgnoreCase(WINDOWS)) {
            declareDefault(SystemDSLinkConstants.RUN_WINDOW_SCRIPT, makeReadWMICData());
        } else if (si.getOperatingSystem().getFamily().equalsIgnoreCase(LINUX)) {
            declareDefault(SystemDSLinkConstants.RUN_LINUX_SCRIPT, makeRunLinuxCMD());
        }
    }

    private void startTimer(int seconds) {
        // Use DSRuntime for timers and its thread pool.
        timer = DSRuntime.run(this, System.currentTimeMillis() + (seconds * 1000), (seconds * 1000));
    }

    private void stopTimer() {
        timer.cancel();
    }

    /**
     * Handles the reset action.
     */
    @Override
    public ActionResult onInvoke(DSInfo actionInfo, ActionInvocation invocation) {

        return super.onInvoke(actionInfo, invocation);
    }

    // System information not changing every sec.
    private void systemInot() {
        //DSLink Settings
        put(SystemDSLinkConstants.DIAGNOSTICS_MODE, DSBool.valueOf(false));
        put(SystemDSLinkConstants.POLL_RATE, DSInt.valueOf(1));

        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();
        CentralProcessor processor = hal.getProcessor();

        //--Hostname
        String hostName = "Unknown";
        try {
            hostName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //NetworkParams networkParams = os.getNetworkParams();
        putA(SystemDSLinkConstants.HOST_NAME, DSString.valueOf(hostName));
        //Architecture
        putA(SystemDSLinkConstants.ARCHITECTURE, DSString.valueOf(System.getProperty("os.arch")));
        //Operating System
        putA(SystemDSLinkConstants.OPERATING_SYSTEM, DSString.valueOf(os));
        //Platform
        putA(SystemDSLinkConstants.PLATFORM, DSString.valueOf(os.getFamily()));
        //Processes
        putA(SystemDSLinkConstants.PROCESSES, DSString.valueOf(os.getProcessCount()));
        //--Processor Count
        putA(SystemDSLinkConstants.PROCESSOR_COUNT, DSString.valueOf(java.lang.management.ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors()));
        //Processor Model
        putA(SystemDSLinkConstants.PROCESSOR_MODEL, DSString.valueOf(processor));
        //Model
        putA(SystemDSLinkConstants.MODEL, DSString.valueOf(processor.toString()));

    }

    // Values needs to be refreshed based on value
    private void updateMetrics(){
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();
        FileSystem fileSystem = os.getFileSystem();
        CentralProcessor processor = hal.getProcessor();

        //System Time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
        LocalDateTime now = LocalDateTime.now();
        putA(SystemDSLinkConstants.SYSTEM_TIME, DSString.valueOf(dtf.format(now)));
        //-- CPU Usage
        putA(SystemDSLinkConstants.CPU_USAGE, DSString.valueOf( Util.round((100f * processor.getSystemCpuLoad()),0) + " %" ));
        //Battery Level
        PowerSource[] powerSource = hal.getPowerSources();
        putA(SystemDSLinkConstants.BATTERY_LEVEL, DSString.valueOf( Util.round(powerSource[0].getRemainingCapacity() * 100,0) + " %"));

        //--Disk space
        OSFileStore[] fsArray = fileSystem.getFileStores();
        long usable = 0;
        long total = 0;
        for(int index = 0; index < fsArray.length; index++)  {
            usable = usable + fsArray[index].getUsableSpace();
            total = total + fsArray[index].getTotalSpace();
        }
        putA(SystemDSLinkConstants.TOTAL_DISK_SPACE, DSString.valueOf(Util.formatBytes(total,"mb") ));
        putA(SystemDSLinkConstants.USED_DISK_SPACE, DSString.valueOf(Util.formatBytes(total-usable,"mb")));
        putA(SystemDSLinkConstants.FREE_DISK_SAPCE, DSString.valueOf(Util.formatBytes(usable,"mb")));
        putA(SystemDSLinkConstants.DISK_USAGE, DSString.valueOf(  Util.round((100 - (100d * usable / total)),2) + " %" ));

        //--Memory
        GlobalMemory memory = hal.getMemory();
        long totalMemory = memory.getTotal();
        long freeMemory = memory.getAvailable();
        long usedMemory = totalMemory - freeMemory;
        float memoryUsagePer = 100f *(usedMemory)/totalMemory ;
        putA(SystemDSLinkConstants.TOTAL_MEMORY, DSString.valueOf(Util.formatBytes(totalMemory,"mb") ));
        putA(SystemDSLinkConstants.USED_MEMORY, DSString.valueOf(Util.formatBytes(usedMemory,"mb") ));
        putA(SystemDSLinkConstants.FREE_MEMORY, DSString.valueOf( Util.formatBytes(freeMemory,"mb") ));
        putA(SystemDSLinkConstants.MEMORY_USAGE, DSString.valueOf(Util.round(memoryUsagePer,2) + " %" ));

        //Open files
        putA(SystemDSLinkConstants.OPEN_FILES, DSString.valueOf(fileSystem.getOpenFileDescriptors()));
        //Hardware Identifier
        putA(SystemDSLinkConstants.HARDWARE_IDENTIFIER, DSString.valueOf("Hardware Identifier"));
    }

    private void putA(String metrickey, DSIValue value) {
        DSInfo info = getInfo(metrickey);
        if(info==null) {
            declareDefault(metrickey, value).setReadOnly(true);
        } else {
            put(info, value);
        }
    }

    private DSAction makeExecuteCommand() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                return ((SystemDSLink) info.getParent()).executeCommand(this, info, invocation.getParameters());
            }
        };
        act.addParameter(SystemDSLinkConstants.COMMAND, DSValueType.STRING, null);
        act.setResultType(ActionSpec.ResultType.VALUES);
        act.addValueResult("output", DSValueType.DYNAMIC);
        act.addValueResult("exitCode", DSValueType.NUMBER);
        return act;
    }

    private ActionResult executeCommand(DSAction action, DSInfo actionInfo, DSMap parameters){
        StringBuffer response = new StringBuffer();
        Integer exitCode = -1;
        try {
            Process process = Runtime.getRuntime().exec(parameters.getString(SystemDSLinkConstants.COMMAND));
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append("\n");
            }
            exitCode = process.exitValue();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return getActionResponse(action, e.getMessage(), exitCode);
        }
        return getActionResponse(action, response.toString(), exitCode);
    }


    private DSAction makeExecuteCommandStream() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                return ((SystemDSLink) info.getParent()).executeOutputStream(this, invocation.getParameters());
            }
        };
        act.addParameter(SystemDSLinkConstants.COMMAND, DSValueType.STRING, null);
        act.setResultType(ActionSpec.ResultType.CLOSED_TABLE);
        return act;
    }

    private ActionTable executeOutputStream(DSAction action, DSMap parameters) {

        List<DSMap> cols = new LinkedList<DSMap>();
        cols.add(Util.makeColumn("type", DSValueType.STRING));
        cols.add(Util.makeColumn("value", DSValueType.STRING));

        ArrayList<String> list = new ArrayList<String>();

        try {
            Process process = Runtime.getRuntime().exec(parameters.getString(SystemDSLinkConstants.COMMAND));
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator iterator = list.iterator();

        return getOutputStreamActionTable(action, cols, iterator);
    }

    private ActionTable getOutputStreamActionTable(final DSAction action, final List<DSMap> cols, final Iterator iterator) {

        return new ActionTable() {
            @Override
            public ActionSpec getAction() {
                return action;
            }

            @Override
            public void onClose() {

            }

            @Override
            public boolean next() {
                if(iterator.hasNext()) {
                    return true;
                }
                return false;
            }

            @Override
            public int getColumnCount() {
                return cols.size();
            }

            @Override
            public void getMetadata(int index, DSMap bucket) {
                bucket.putAll(cols.get(index));
            }

            @Override
            public DSIValue getValue(int index) {
                if(index == 0){
                    return DSString.valueOf("stdout");
                }
                return DSString.valueOf(iterator.next());
            }
        };
    }

    private DSAction makeRunAppleScript() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                return ((SystemDSLink) info.getParent()).runAppleScript(this, info, invocation.getParameters());
            }
        };
        act.addParameter(SystemDSLinkConstants.SCRIPT, DSValueType.DYNAMIC, null);
        act.setResultType(ActionSpec.ResultType.VALUES);
        act.addValueResult("output", DSValueType.DYNAMIC);

        return act;
    }

    private ActionResult runAppleScript(DSAction action, DSInfo actionInfo, DSMap parameters) {
        String message = "Not Implemented Apple Script !!!";
        Runtime runtime = Runtime.getRuntime();

        String appleCMD = "display alert \"Hello, world!\" buttons {\"Rudely decline\", \"Happily accept\"}\n" +
                "set theAnswer to button returned of the result\n" +
                "if theAnswer is \"Happily accept\" then\n" +
                "\tbeep 5\n" +
                "else\n" +
                "\tsay \"Piffle!\"\n" +
                "end if\n";

        String sample = "set resultAlertReply to display alert \"Alert Text\" ¬\n" +
                "\tas warning ¬\n" +
                "\tbuttons {\"Skip\", \"Okay\", \"Cancel\"} ¬\n" +
                "\tdefault button 2 ¬\n" +
                "\tcancel button 1 ¬\n" +
                "\tgiving up after 2";


        try
        {
            if(parameters.getString(SystemDSLinkConstants.SCRIPT) == null) {
                String[] args = { "osascript", "-e", appleCMD };
                Process process = runtime.exec(args);
            } else {
                String[] args = { "osascript", "-e", parameters.getString(SystemDSLinkConstants.SCRIPT) };
                Process process = runtime.exec(args);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return getActionResponse(action, message, 0);
    }

    private DSAction makeReadWMICData() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                return ((SystemDSLink) info.getParent()).readWMICData(this, info, invocation.getParameters());
            }
        };
        act.addParameter(SystemDSLinkConstants.SCRIPT, DSValueType.DYNAMIC, null);
        act.setResultType(ActionSpec.ResultType.CLOSED_TABLE);
        act.addValueResult("output", DSValueType.DYNAMIC);

        return act;
    }

    private ActionResult readWMICData(DSAction action, DSInfo actionInfo, DSMap parameters) {
        String message = "Not Implemented WMIC DATA !!!";
        return getActionResponse(action, message, 0);
    }

    private DSAction makeRunLinuxCMD() {
        DSAction act = new DSAction() {
            @Override
            public ActionResult invoke(DSInfo info, ActionInvocation invocation) {
                return ((SystemDSLink) info.getParent()).runLinuxCMD(this, info, invocation.getParameters());
            }
        };
        act.addParameter(SystemDSLinkConstants.SCRIPT, DSValueType.DYNAMIC, null);
        act.setResultType(ActionSpec.ResultType.CLOSED_TABLE);
        act.addValueResult("output", DSValueType.DYNAMIC);

        return act;
    }

    private ActionResult runLinuxCMD(DSAction action, DSInfo actionInfo, DSMap parameters) {
        String message = "Not Implemented Linux CMD !!!";
        return getActionResponse(action, message, 0);
    }

    private ActionResult getActionResponse(DSAction action,String response, Integer exitCode){
        DSActionValues result = new DSActionValues(action);
        result.addResult(DSString.valueOf(response));
        result.addResult(DSInt.valueOf(exitCode));
        return result;
    }

    private void removeDiagnosticsModeNode() {
        DSInfo info = getInfo(SystemDSLinkConstants.PROCESS_NODE);
        remove(info);
    }
}
