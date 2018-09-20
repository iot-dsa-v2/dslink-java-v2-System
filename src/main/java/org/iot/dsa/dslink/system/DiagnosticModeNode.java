package org.iot.dsa.dslink.system;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;

import java.util.function.Consumer;

public class DiagnosticModeNode extends DSNode {

    private Integer pollRate = 0;

    public DiagnosticModeNode() {

    }

    public DiagnosticModeNode(Integer pollRate) {
        this.pollRate = pollRate;
    }

    @Override
    protected void declareDefaults() {
        super.declareDefaults();
    }

    @Override
    protected void onStable() {
        super.onStable();
        createDataFlowNode();
    }

    @Override
    protected void onSubscribed() {
        super.onSubscribed();
    }

    private void createDataFlowNode() {
        for(int i = 0; i < 5; i++) {
            put("DF"+i, new DataFlowNode("sampleCMD :"+Math.round(Math.random()*10), "memoryUsage :"+i, "openFiles :"+i));
        }
    }
}
