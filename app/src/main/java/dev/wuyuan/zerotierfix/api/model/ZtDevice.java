package dev.wuyuan.zerotierfix.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ZeroTier Device Model
 * Represents a device/member in a ZeroTier network
 */
public class ZtDevice {
    private String nodeId;
    private String name;
    private boolean online;
    private List<String> ipAddresses;

    public ZtDevice() {
        this.ipAddresses = new ArrayList<>();
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public List<String> getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public String getPrimaryIp() {
        if (ipAddresses != null && !ipAddresses.isEmpty()) {
            return ipAddresses.get(0);
        }
        return "";
    }
}
