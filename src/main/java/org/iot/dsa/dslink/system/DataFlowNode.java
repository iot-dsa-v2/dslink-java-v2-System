package org.iot.dsa.dslink.system;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.node.DSNode;
import org.json.JSONObject;

import java.util.Iterator;

public class DataFlowNode extends DSNode implements Runnable {

    private String cmd;
    private String memUsg;
    private String opnFl;
    private String filePath;
    private Integer pollRate;
    private DSRuntime.Timer timer;

    public DataFlowNode() {

    }

    public DataFlowNode(Integer pollRate, String filePath, String cmd, String memUsg, String opnFl) {
        this.pollRate = pollRate;
        this.cmd = cmd;
        this.memUsg = memUsg;
        this.opnFl = opnFl;
        this.filePath = filePath;
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
        //startTimer(1);
    }

    private void updateDataNodeMetrics() {

        JSONObject response = Util.calculatePID(this.filePath);
        Iterator resIterator = response.keys();

        while(resIterator.hasNext()) {
            JSONObject details = (JSONObject) response.get((String) resIterator.next());

            put(SystemDSLinkConstants.COMMAND, details.getString("Command"));
            put(SystemDSLinkConstants.MEMORY_USAGE, details.getString("MemoryUsage"));
            put(SystemDSLinkConstants.OPEN_FILES, details.getString("OpenFile"));
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
