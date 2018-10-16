package org.iot.dsa.dslink.system;

import org.iot.dsa.node.DSNode;
import org.json.JSONObject;

import java.util.Iterator;

public class DiagnosticModeNode extends DSNode {

    private Integer pollRate = 0;
    private String filePath = null;

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
        JSONObject response = Util.calculatePID();
        Iterator resIterator = response.keys();

        while(resIterator.hasNext()) {
            JSONObject details = (JSONObject) response.get((String) resIterator.next());
            String linkName = details.getString("LinkName");
            String cmd = details.getString("Command");
            String memUsg = details.getString("MemoryUsage");
            String opnFl = details.getString("OpenFile");
            put(linkName, new DataFlowNode(this.pollRate, this.filePath, cmd, memUsg, opnFl));
        }
    }
}
