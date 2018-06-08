package com.hisense.etl.util.pools;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TransportWrapper {

    private TransportClient transportClient;

    /**
     * 是否正忙
     */
    private boolean isBusy = false;

    /**
     * 是否已经挂
     */
    private boolean isDead = false;

    /**
     * 最后使用时间
     */
    private Date lastUseTime;

    /**
     * cluster name
     */
    private String clusterName;

    /**
     * 服务端Server主机名或IP
     */
    private String host;

    /**
     * 服务端Port
     */
    private int port;

    public TransportWrapper(TransportClient transportClient, String clusterName, boolean isOpen) {
        this.lastUseTime = new Date();
        this.transportClient = transportClient;
        this.clusterName = clusterName;
//        this.host = host;
//        this.port = port;
        if (isOpen) {
            try {
                Settings setting = Settings.builder().put("cluster.name", clusterName)
                        .put("client.transport.sniff", true).build();
                transportClient = new PreBuiltTransportClient(setting);
            } catch (Exception e) {
                //e.printStackTrace();
                System.err.println(clusterName + ":" + host + ":" + port + " " + e.getMessage());
                isDead = true;
            }
        }
    }

    public TransportWrapper(TransportClient transportClient, String clusterName) {
        this(transportClient, clusterName, false);
    }


    public boolean isBusy() {
        return isBusy;
    }

    public void setIsBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setIsDead(boolean isDead) {
        this.isDead = isDead;
    }

    public TransportClient getTransportClient() {
        return transportClient;
    }

    public void setTransportClient(TransportClient transportClient) {
        this.transportClient = transportClient;
    }

    /**
     * 当前transport是否可用
     *
     * @return
     */
    public boolean isAvailable() {
        return !isBusy && !isDead ;
    }

    public Date getLastUseTime() {
        return lastUseTime;
    }

    public void setLastUseTime(Date lastUseTime) {
        this.lastUseTime = lastUseTime;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "hashCode:" + hashCode() + "," +
                host + ":" + port + ",isBusy:" + isBusy + ",isDead:" + isDead + ",isOpen:" +
                ",isAvailable:" + isAvailable() + ",lastUseTime:" + format.format(lastUseTime);
    }
}
