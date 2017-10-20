package at.spc.bean.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Terry LIANG on 2017/9/30.
 */
public class ServiceNode {
    private String nodeId;

    private Map<String, ServiceSubNode> subNodes = new ConcurrentHashMap<String, ServiceSubNode>();

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Map<String, ServiceSubNode> getSubNodes() {
        return subNodes;
    }
}
