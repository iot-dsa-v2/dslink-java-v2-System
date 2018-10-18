package org.iot.dsa.dslink.system;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.node.DSNode;

public class DataFlowNode extends DSNode implements Runnable {

    private Integer pid;
    private Integer pollRate;
    private DSRuntime.Timer timer;

    public DataFlowNode() {

    }

    public DataFlowNode(Integer pid, Integer pollRate) {
        this.pollRate = pollRate;
        this.pid = pid;
    }

    @Override
    protected void declareDefaults() {
        super.declareDefaults();
    }


    @Override
    protected void onStable() {
        super.onStable();
        setDataNodeMetrics();
        startTimer(this.pollRate);
    }

    private void setDataNodeMetrics() {
        String[] arr = Util.getPIDInfo(pid);
        put(SystemDSLinkConstants.COMMAND, "Command");
        put(SystemDSLinkConstants.MEMORY_USAGE, arr[0]);
        put(SystemDSLinkConstants.OPEN_FILES, arr[1]);
    }

    /**
     * Called by the timer, increments the counter.
     */
    @Override
    public void run() {
        setDataNodeMetrics();
    }

    private void startTimer(int seconds) {
        timer = DSRuntime.run(this, System.currentTimeMillis() + (seconds * 1000), (seconds * 1000));
    }

    private void stopTimer() {
        timer.cancel();
    }
}
