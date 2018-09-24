package org.iot.dsa.dslink.system;

import org.iot.dsa.DSRuntime;
import org.iot.dsa.io.json.JsonReader;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.function.Consumer;

public class DiagnosticModeNode extends DSNode {

    private Integer pollRate = 0;
    private String filePath = null;

    public DiagnosticModeNode() {

    }

    public DiagnosticModeNode(Integer pollRate, String filePath) {
        this.pollRate = pollRate;
        this.filePath = filePath;
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
        JSONObject response = Util.calculatePID(this.filePath);
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
