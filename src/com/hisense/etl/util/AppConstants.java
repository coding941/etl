package com.hisense.etl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

public final class AppConstants {

	private static final Logger log = LoggerFactory.getLogger(Class.class);
	/**
	 * the number of thread for parse excel and 4es
	 */
	public static final float PRODUCER_CORE_SIZE_FACTOR;
	public static final float CONSUMER_CORE_SIZE_FACTOR;
	public static final int BLOCK_CACHE_SIZE;
	public static final String CLUSTER_NAME;
	public static final String CLUSTER_SERVER_IPS;
	public static final String COMMON_ATTRI_TYPE;

	private final static String PARAMETER_FILE_NAME="app.properties";
	
	static {
		Properties prop=new Properties();
		try {
//			prop.load(new InputStreamReader(AppConstants.class.getClassLoader().getResourceAsStream("app.properties"), "UTF-8"));
			String filePath=AppConstants.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			filePath = filePath.substring(1, filePath.lastIndexOf("/") + 1);
			filePath = URLDecoder.decode(filePath,"UTF-8");
			log.info("filePath:"+filePath);
			prop.load(new InputStreamReader(new FileInputStream(filePath+PARAMETER_FILE_NAME),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		PRODUCER_CORE_SIZE_FACTOR = Float.parseFloat(prop.get("producer_core_size_factor").toString());
		CONSUMER_CORE_SIZE_FACTOR = Float.parseFloat(prop.get("consumer_core_size_factor").toString());
		BLOCK_CACHE_SIZE = 1<<Integer.parseInt(prop.get("block_cache_size").toString());

		CLUSTER_NAME = prop.get("cluster_name").toString();
		CLUSTER_SERVER_IPS = prop.get("cluster_server_ips").toString();

		COMMON_ATTRI_TYPE = prop.get("common_attri_type").toString();
		log.info("configuration file successfully loaded.");
	}

}
