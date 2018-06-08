package com.hisense.etl.util.pools;


public class ServerInfo {

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private String clusterName;
    private String host;
    private int port;

    public ServerInfo(String clusterName,String host, int port) {
        this.clusterName = clusterName;
        this.host = host;
        this.port = port;
    }

    public String toString() {
        return "clusterName:" + clusterName + "host:" + host + ",port:" + port;
    }
}
