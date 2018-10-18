package org.iot.dsa.dslink.system;

import org.iot.dsa.node.DSNode;

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
        String[] strArr = Util.getPIDList();
        for (int index = 0; index < strArr.length; index = index + 2) {
            String pid = strArr[index].substring(1, strArr[index].length()-1);
            String linkName = strArr[index+1].substring(1, strArr[index+1].length()-1);
            put(linkName, new DataFlowNode(Integer.parseInt(pid), this.pollRate));
        }
    }
}
