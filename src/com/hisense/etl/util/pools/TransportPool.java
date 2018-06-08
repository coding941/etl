package com.hisense.etl.util.pools;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * transportclient连接池
 *
 * @version : 0.1 BETA
 * @since : 2018-5-23
 */
public class TransportPool {

    Semaphore access = null;
    TransportWrapper[] pool = null;
    int poolSize = 1;//连接池大小
    int minSize = 1;//池中保持激活状态的最少连接个数
    int maxIdleSecond = 300;//最大空闲时间（秒），超过该时间的空闲时间的连接将被关闭
    int checkInvervalSecond = 60;//每隔多少秒，检测一次空闲连接（默认60秒）
    List<ServerInfo> serverInfos;
    boolean allowCheck = true;
    Thread checkTread = null;

    public int getCheckInvervalSecond() {
        return checkInvervalSecond;
    }

    public void setCheckInvervalSecond(int checkInvervalSecond) {
        this.checkInvervalSecond = checkInvervalSecond;
    }


    /**
     * 连接池构造函数
     *
     * @param poolSize            连接池大小
     * @param minSize             池中保持激活的最少连接数
     * @param maxIdleSecond       单个连接最大空闲时间，超过此值的连接将被断开
     * @param checkInvervalSecond 每隔多少秒检查一次空闲连接
     * @param serverList          服务器列表
     */
    public TransportPool(int poolSize, int minSize, int maxIdleSecond, int checkInvervalSecond, List<ServerInfo> serverList) {
        if (poolSize <= 0) {
            poolSize = 1;
        }
        if (minSize > poolSize) {
            minSize = poolSize;
        }
        if (minSize <= 0) {
            minSize = 0;
        }
        this.maxIdleSecond = maxIdleSecond;
        this.minSize = minSize;
        this.poolSize = poolSize;
        this.serverInfos = serverList;
        this.allowCheck = true;
        this.checkInvervalSecond = checkInvervalSecond;
        init();
        check();
    }

    /**
     * 连接池构造函数（默认最大空闲时间300秒）
     *
     * @param poolSize   连接池大小
     * @param minSize    池中保持激活的最少连接数
     * @param serverList 服务器列表
     */
    public TransportPool(int poolSize, int minSize, List<ServerInfo> serverList) {
        this(poolSize, minSize, 300, 60, serverList);
    }


    public TransportPool(int poolSize, List<ServerInfo> serverList) {
        this(poolSize, 1, 300, 60, serverList);
    }

    public TransportPool(List<ServerInfo> serverList) {
        this(serverList.size(), 1, 300, 60, serverList);
    }


    /**
     * 检查空闲连接
     */
    private void check() {
        checkTread =
                new Thread(new Runnable() {
                    public void run() {
                        while (allowCheck) {
                            //System.out.println("--------------");
                            System.out.println("check idle connection...");
                            for (int i = 0; i < pool.length; i++) {
                                //if (pool[i] == null) {
                                //    System.out.println("pool[" + i + "]为null");
                                //}
                                //if (pool[i].getTransport() == null) {
                                //    System.out.println("pool[" + i + "].getTransport()为null");
                                //}
                                if (pool[i].isAvailable() && pool[i].getLastUseTime() != null) {
                                    long idleTime = new Date().getTime() - pool[i].getLastUseTime().getTime();
                                    //超过空闲阀值的连接，主动断开，以减少资源消耗
                                    if (idleTime > maxIdleSecond * 1000) {
                                        if (getActiveCount() > minSize) {
                                            pool[i].getTransportClient().close();
                                            pool[i].setIsBusy(false);
                                            System.out.println(pool[i].hashCode() + "," + pool[i].getHost() + ":" + pool[i].getPort() + " 超过空闲时间阀值被断开！");
                                        }
                                    }
                                }
                            }
                            System.out.println("当前活动连接数：" + getActiveCount());
                            try {
                                Thread.sleep(checkInvervalSecond * 1000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        checkTread.start();
    }

    /**
     * 连接池初始化
     */
    private void init() {
        access = new Semaphore(poolSize);
        pool = new TransportWrapper[poolSize];

        for (int i = 0; i < pool.length; i++) {
            int j = i % serverInfos.size();
            Settings setting = Settings.builder().put("cluster.name", serverInfos.get(i).getClusterName())
                    .put("client.transport.sniff", true).build();
            TransportClient transportClient = new PreBuiltTransportClient(setting);
            if (i < minSize) {
                pool[i] = new TransportWrapper(transportClient, serverInfos.get(i).getClusterName(), true);
//                pool[i] = new TransportWrapper(socket, serverInfos.get(j).getHost(), serverInfos.get(j).getPort(), true);
            } else {
                pool[i] = new TransportWrapper(transportClient, serverInfos.get(i).getClusterName());
//                pool[i] = new TransportWrapper(socket, serverInfos.get(j).getHost(), serverInfos.get(j).getPort());
            }
        }
    }


    /**
     * 从池中取一个可用连接
     * @return
     */
    public TransportClient get() {
        try {
            if (access.tryAcquire(3, TimeUnit.SECONDS)) {
                synchronized (this) {
                    for (int i = 0; i < pool.length; i++) {
                        if (pool[i].isAvailable()) {
                            pool[i].setIsBusy(true);
                            pool[i].setLastUseTime(new Date());
                            return pool[i].getTransportClient();
                        }
                    }
                    //尝试激活更多连接
                    for (int i = 0; i < pool.length; i++) {
                        if (!pool[i].isBusy() && !pool[i].isDead()
                                /* && !pool[i].getTransportClient().isOpen() */) {
                            try {
//                                pool[i].getTransportClient().open();
                                pool[i].setIsBusy(true);
                                pool[i].setLastUseTime(new Date());
                                return pool[i].getTransportClient();
                            } catch (Exception e) {
                                //e.printStackTrace();
                                System.err.println(pool[i].getHost() + ":" + pool[i].getPort() + " " + e.getMessage());
                                pool[i].setIsDead(true);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("can not get available client");

        }
        throw new RuntimeException("all client is busy");
    }

    /**
     * 客户端调用完成后，必须手动调用此方法，将TransportClient恢复为可用状态
     *
     * @param client
     */
    public void release(TransportClient client) {
        boolean released = false;
        synchronized (this) {
            for (int i = 0; i < pool.length; i++) {
                if (client == pool[i].getTransportClient() && pool[i].isBusy()) {
                    pool[i].setIsBusy(false);
                    released = true;
                    break;
                }
            }
        }
        if (released) {
            access.release();
        }
    }


    public void destory() {
        if (pool != null) {
            for (int i = 0; i < pool.length; i++) {
                pool[i].getTransportClient().close();
            }
        }
        allowCheck = false;
        checkTread = null;
        System.out.print("连接池被销毁！");
    }

    /**
     * 获取当前已经激活的连接数
     *
     * @return
     */
    public int getActiveCount() {
        int result = 0;
        for (int i = 0; i < pool.length; i++) {
            if (!pool[i].isDead() /* && pool[i].getTransportClient().isOpen() */) {
                result += 1;
            }
        }
        return result;
    }

    /**
     * 获取当前繁忙状态的连接数
     *
     * @return
     */
    public int getBusyCount() {
        int result = 0;
        for (int i = 0; i < pool.length; i++) {
            if (!pool[i].isDead() && pool[i].isBusy()) {
                result += 1;
            }
        }
        return result;
    }

    /**
     * 获取当前已"挂"掉连接数
     *
     * @return
     */
    public int getDeadCount() {
        int result = 0;
        for (int i = 0; i < pool.length; i++) {
            if (pool[i].isDead()) {
                result += 1;
            }
        }
        return result;
    }

    public String toString() {
        return "poolsize:" + pool.length +
                ",minSize:" + minSize +
                ",maxIdleSecond:" + maxIdleSecond +
                ",checkInvervalSecond:" + checkInvervalSecond +
                ",active:" + getActiveCount() +
                ",busy:" + getBusyCount() +
                ",dead:" + getDeadCount();
    }

    public String getWrapperInfo(TransportClient client) {
        for (int i = 0; i < pool.length; i++) {
            if (pool[i].getTransportClient() == client) {
                return pool[i].toString();
            }
        }
        return "";
    }
}
