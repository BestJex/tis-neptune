/* 
 * The MIT License
 *
 * Copyright (c) 2018-2022, qinglangtech Ltd
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.qlangtech.tis.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.qlangtech.tis.manage.common.ConfigFileContext;
import com.qlangtech.tis.manage.common.ConfigFileContext.StreamProcess;
import com.qlangtech.tis.pubhook.common.RunEnvironment;

/*
 * 从diamond上获取 terminatorbean 需要的配置信息
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TSearcherConfigFetcher {

    private static final Logger logger = LoggerFactory.getLogger(TSearcherConfigFetcher.class);

    public static final String CONFIG_LOG_FLUME_AGENT_ADDRESS = "log_flume_agent";

    public static final String CONFIG_ZKADDRESS = "zkaddress";

    public static final String CONFIG_HDFS_ADDRESS = "hdfsaddress";

    public static final String CONFIG_runenvironment = "runenvironment";

    private static final String CONFIG_terminator_host_address = "terminator_host_address";

    private static final String jobtracker_rpcserver = "jobtracker_rpcserver";

    private static final String jobtracker_transserver = "jobtracker_transserver";

    private static final String LOG_SOURCE_ADDRESS = "log_source_address";

    private List<String> mqStatisticsHost;

    private final String indexBuildCenterHost;

    // 组装节点
    private final String assembleHost;

    // 终搜后台地址
    private final String terminatorConsoleHost;

    private final String zkAddress;

    // 线上zk地址 在用户需要打通线上和预发的时候使用
    private String onlineZkAddress;

    private final String hdfsAddress;

    private final String runEnvironment;

    private final String logFlumeAgent;

    // private final String cnaddress;
    // private final Integer searcherServerPort;
    // private final String hbaseAddress;
    private String logSourceAddress;

    private final String job_rpcserver;

    private final String job_transserver;

    // online:10.162.48.129:10001
    private final String hiveHost;

    // 最大数据库导入线程数目
    private final Integer maxDBDumpThreadCount;

    private TSearcherConfigFetcher(String serviceName) {
        final RunEnvironment runtime = RunEnvironment.getSysRuntime();
        ServiceConfig servceConfig = null;
        try {
            URL url = new URL(runtime.getInnerRepositoryURL() + "/config/config.ajax?action=global_parameters_config_action&event_submit_do_get_all=y&runtime=" + runtime.getKeyName() + "&resulthandler=advance_query_result");
            // Exception e = new Exception();
            // e.printStackTrace();
            System.out.println("apply url:" + url);
            List<KeyPair> pairs = ConfigFileContext.processContent(url, new StreamProcess<List<KeyPair>>() {

                @Override
                public List<KeyPair> p(int status, InputStream stream, String md5) {
                    try {
                        return JSON.parseArray(IOUtils.toString(stream, Charset.forName("utf8")), KeyPair.class);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, 20);
            servceConfig = new ServiceConfig(pairs);
        } catch (MalformedURLException e1) {
            throw new RuntimeException(e1);
        }
        // build中心host地址
        this.indexBuildCenterHost = servceConfig.getString("index_build_center_host");
        Assert.assertNotNull("indexBuildCenterHost can not be null,key:index_build_center_host", indexBuildCenterHost);
        // hbaseAddress = servceConfig.getString("hbase_address");
        this.assembleHost = servceConfig.getString("tis_assemble_host");
        this.logFlumeAgent = servceConfig.getString(CONFIG_LOG_FLUME_AGENT_ADDRESS);
        this.mqStatisticsHost = Arrays.asList(StringUtils.split(servceConfig.getString("mq_statistics_host"), ","));
        this.maxDBDumpThreadCount = servceConfig.getInteger("max_db_dump_thread_count");
        // log.info("max_db_dump_thread_count:" + this.maxDBDumpThreadCount);
        this.hiveHost = servceConfig.getString("hivehost");
        zkAddress = servceConfig.getString(CONFIG_ZKADDRESS);
        // json.getString(CONFIG_ZKADDRESS);
        hdfsAddress = servceConfig.getString(CONFIG_HDFS_ADDRESS);
        // ;
        runEnvironment = servceConfig.getString(CONFIG_runenvironment);
        // this.searcherServerPort = Integer.parseInt(servceConfig
        // .getString("search_export_port"));
        this.terminatorConsoleHost = servceConfig.getString(CONFIG_terminator_host_address);
        try {
            this.logSourceAddress = servceConfig.getString(LOG_SOURCE_ADDRESS);
        } catch (Throwable e) {
        }
        try {
            this.onlineZkAddress = servceConfig.getString("online_zkaddress");
        } catch (Throwable e) {
        }
        this.job_rpcserver = servceConfig.getString(jobtracker_rpcserver);
        this.job_transserver = servceConfig.getString(jobtracker_transserver);
        // cnetnernode地址
        // this.cnaddress = servceConfig.getString(CONFIG_cnaddress);
        // try {
        // this.tisHost = servceConfig.getString("tis_host");
        // } catch (Throwable e) {
        // 
        // }
        // } catch (JSONException e) {
        // throw new RuntimeException("TERMINAOTR_BEAN_CONFIG:"
        // + TERMINAOTR_BEAN_CONFIG, e);
        // }
        // Assert.assertNotNull("hbase can not be null", hbaseAddress);
        Assert.assertNotNull("zkaddress can not be null", zkAddress);
        Assert.assertNotNull("hdfsAddress can not be null", hdfsAddress);
        Assert.assertNotNull("runEnvironment can not be null", runEnvironment);
    // Assert.assertNotNull("cnaddress can not be null", this.cnaddress);
    }

    private static class ServiceConfig {

        // private final JSONObject globalConfig;
        // private final JSONObject localConfig;
        // ServiceConfig(DefaultDiamondManager globalManager,
        // DefaultDiamondManager localDiamond) throws JSONException {
        // super();
        // String jsonConfig = globalManager.getConfigureInfomation(6000);
        // Assert.assertNotNull("jsonConfig can not be null", jsonConfig);
        // JSONTokener tokener = new JSONTokener(jsonConfig);
        // globalConfig = new JSONObject(tokener);
        // 
        // jsonConfig = localDiamond.getConfigureInfomation(6000);
        // tokener = new JSONTokener((jsonConfig == null) ? "{}" : jsonConfig);
        // localConfig = new JSONObject(tokener);
        // }
        // private final ResourceBundle resourceBundle;
        private Map<String, String> resourceBundle = new HashMap<String, String>();

        public ServiceConfig(List<KeyPair> pairs) {
            for (KeyPair p : pairs) {
                resourceBundle.put(p.getKey(), p.getValue());
            }
        }

        public String getString(String key) {
            try {
                return resourceBundle.get(key);
            } catch (Throwable e) {
            }
            return StringUtils.EMPTY;
        }

        public Long getLong(String key) {
            try {
                return Long.parseLong(resourceBundle.get(key));
            } catch (Throwable e) {
            }
            return null;
        }

        public Integer getInteger(String key) {
            try {
                return Integer.parseInt(resourceBundle.get(key));
            } catch (Throwable e) {
            }
            return null;
        }
    }

    // private static JSONObject parseJson(DefaultDiamondManager globalManager)
    // throws JSONException {
    // String jsonConfig = globalManager.getConfigureInfomation(6000);
    // Assert.assertNotNull("jsonConfig can not be null", jsonConfig);
    // JSONTokener tokener = new JSONTokener(jsonConfig);
    // return new JSONObject(tokener);
    // }
    // private static TSearcherConfigFetcher globalConfig;
    // 
    // private static TSearcherConfigFetcher getGlobalInstance() {
    // 
    // if (globalConfig == null) {
    // synchronized (TSearcherConfigFetcher.class) {
    // if (globalConfig == null) {
    // globalConfig = new TSearcherConfigFetcher(StringUtils.EMPTY);
    // }
    // }
    // }
    // 
    // return globalConfig;
    // }
    public String getAssembleHost() {
        String assembleHost = getInstance().assembleHost;
        if (StringUtils.isBlank(assembleHost)) {
            throw new IllegalStateException("param assembleHost can not be null");
        }
        return assembleHost;
    }

    public String getHiveHost() {
        return getInstance().hiveHost;
    }

    public RunEnvironment getRuntime() {
        return RunEnvironment.getSysRuntime();
    }

    public String getLogFlumeAddress() {
        return getInstance().logFlumeAgent;
    }

    public String getJobRpcserver() {
        return getInstance().job_rpcserver;
    }

    public String getJobTransserver() {
        return getInstance().job_transserver;
    }

    public Integer getMaxDBDumpThreadCount() {
        return getInstance().maxDBDumpThreadCount;
    }

    /**
     * 取得终搜后台地址
     *
     * @return
     */
    public String getTerminatorConsoleHostAddress() {
        return getInstance().terminatorConsoleHost;
    }

    // public String getHbaseAddress() {
    // return hbaseAddress;
    // }
    // public String getCnAddress() {
    // return getInstance().cnaddress;
    // }
    // public int getSolrExportPort() {
    // return getInstance().searcherServerPort;
    // }
    public String getZkAddress() {
        return getInstance().zkAddress;
    }

    public String getOnlineZkAddress() {
        return getInstance().onlineZkAddress;
    }

    /**
     * 南星的MQ访问查询服務端地址
     *
     * @return
     */
    public List<String> getMQStatisticsHost() {
        List<String> mqStatisticsHost = getInstance().mqStatisticsHost;
        if (mqStatisticsHost.size() < 1) {
            throw new IllegalStateException("mqStatisticsHost size can not small than 1");
        }
        return mqStatisticsHost;
    }

    public String getIndexBuildCenterHost() {
        return getInstance().indexBuildCenterHost;
    }

    public String getHdfsAddress() {
        return getInstance().hdfsAddress;
    }

    public String getRunEnvironment() {
        return getInstance().runEnvironment;
    }

    public String getLogSourceAddress() {
        return logSourceAddress;
    }

    private TSearcherConfigFetcher getInstance() {
        return this;
    // if (configFetcher == null) {
    // synchronized (TSearcherConfigFetcher.class) {
    // if (configFetcher == null) {
    // configFetcher = new TSearcherConfigFetcher();
    // }
    // }
    // }
    // 
    // return configFetcher;
    }

    // public static void setConfigCenterHost(String runtime) {
    // RunEnvironment rt = RunEnvironment.getEnum(runtime);
    // System.setProperty(RunEnvironment.KEY_RUNTIME, rt.getKeyName());
    // 
    // if (rt == RunEnvironment.ONLINE) {
    // System.setProperty("globalConfigRepositoryHost",
    // "http://tis.2dfire.info:8080");
    // } else {
    // System.setProperty("globalConfigRepositoryHost",
    // "http://10.1.7.43:8080");
    // }
    // }
    private static final Map<String, TSearcherConfigFetcher> /* indexName */
    indexsConfig = new HashMap<String, TSearcherConfigFetcher>();

    public static TSearcherConfigFetcher get() {
        return getInstance("search4");
    }

    public static TSearcherConfigFetcher getInstance(String serviceName) {
        Assert.assertTrue(StringUtils.startsWith(serviceName, "search4"));
        TSearcherConfigFetcher config = null;
        config = indexsConfig.get(serviceName);
        if (config == null) {
            synchronized (indexsConfig) {
                config = indexsConfig.get(serviceName);
                if (config == null) {
                    config = new TSearcherConfigFetcher(serviceName);
                    indexsConfig.put(serviceName, config);
                }
            }
        }
        return config;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        TSearcherConfigFetcher config = TSearcherConfigFetcher.getInstance("search4sucainew");
        System.out.println(config.getZkAddress());
    // int i = 0;
    // while (i++ < 20) {
    // System.out.println((int) (Math.random() * 5));
    // 
    // }
    }
}
