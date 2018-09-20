package org.iot.dsa.dslink.system;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;

import java.util.ArrayList;

public class DataFlowNode extends DSNode implements Runnable {

    private String cmd;
    private String memUsg;
    private String opnFl;
    private DSRuntime.Timer timer;

    public DataFlowNode() {

    }

    public DataFlowNode(String command, String memoryUsage, String openFiles) {
        this.cmd = command;
        this.memUsg = memoryUsage;
        this.opnFl = openFiles;
    }

    @Override
    protected void declareDefaults() {
        super.declareDefaults();
    }

    private void putValuesDF() {
        put(SystemDSLinkConstants.COMMAND, this.cmd);
        put(SystemDSLinkConstants.MEMORY_USAGE, this.memUsg);
        put(SystemDSLinkConstants.OPEN_FILES, this.opnFl);
    }

    @Override
    protected void onStable() {
        super.onStable();
        putValuesDF();
        startTimer(1);
    }

    private void updateDataNodeMetrics() {

        ArrayList<String> arrli = new ArrayList<String>(5);

        arrli.add("DF0");
        arrli.add("DF1");
        arrli.add("DF2");
        arrli.add("DF3");
        arrli.add("DF4");

        for(int i = 0; i < 5; i++) {

            DSInfo info = getInfo(arrli.get(i));

            Long temp = Math.round(Math.random() * 100);

            if (info.getName().equalsIgnoreCase("DFO") ||
                    info.getName().equalsIgnoreCase("DF1") ||
                    info.getName().equalsIgnoreCase("DF2") ||
                    info.getName().equalsIgnoreCase("DF3") ||
                    info.getName().equalsIgnoreCase("DF4") ) {
                put(SystemDSLinkConstants.COMMAND, temp);
                put(SystemDSLinkConstants.MEMORY_USAGE, temp);
                put(SystemDSLinkConstants.OPEN_FILES, temp);
            }

        }
    }

    /**
     * Called by the timer, increments the counter.
     */
    @Override
    public void run() {
        updateDataNodeMetrics();
    }

    private void startTimer(int seconds) {
        timer = DSRuntime.run(this, System.currentTimeMillis() + (seconds * 1000), (seconds * 1000));
    }

    private void stopTimer() {
        timer.cancel();
    }
}
