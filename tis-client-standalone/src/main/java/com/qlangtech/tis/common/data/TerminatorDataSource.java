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
package com.qlangtech.tis.common.data;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import com.qlangtech.tis.common.data.JDBCProperties.DBMSType;

/*
 * 新的默认DataSource，根据注入的dsXmlPath去解析ds.xml的内容，然后生成DataSource实例
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TerminatorDataSource implements DataSource, JDBCPropertiesSupport {

    protected Log logger = LogFactory.getLog(TerminatorDataSource.class);

    public static final String DATASOURCE_NAME = "name";

    public static final String DATASOURCE_TYPE = "type";

    public static final String DRIVER = "driver";

    public static final String URL = "url";

    public static final String USERNAME = "username";

    public static final String PASSWORD = "password";

    public static final String PROPERTIES = "properties";

    public static final String PROPERTIES_NAME = "name";

    public static final String PROPERTIES_VALUE = "value";

    // 以下两个属性是需要注入的
    /**
     * @uml.property  name="name"
     */
    protected String name;

    /**
     * @uml.property  name="path"
     */
    protected String path;

    /**
     * @uml.property  name="config"
     * @uml.associationEnd
     */
    protected TerminatorDataSourceConfig config = null;

    /**
     * @uml.property  name="jdbcProperties"
     * @uml.associationEnd
     */
    protected JDBCProperties jdbcProperties = null;

    protected long lastModifiedTime = 0;

    public TerminatorDataSource() {
    }

    public TerminatorDataSource(String name, String path) {
        this.name = name;
        this.path = path;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.getDataSource().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.getDataSource().setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.getDataSource().setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.getDataSource().getLoginTimeout();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (T) this.getDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.getDataSource().isWrapperFor(iface);
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return this.getDataSource().getConnection();
        } catch (DataSourceDefinationException dsde) {
            throw new SQLException(dsde);
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        try {
            return this.getDataSource().getConnection(username, password);
        } catch (DataSourceDefinationException dsde) {
            throw new SQLException(dsde);
        }
    }

    /**
     * @return
     * @uml.property  name="name"
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     * @uml.property  name="name"
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     * @uml.property  name="path"
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     * @uml.property  name="path"
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return
     * @uml.property  name="config"
     */
    public TerminatorDataSourceConfig getConfig() {
        return config;
    }

    /**
     * @param config
     * @uml.property  name="config"
     */
    public void setConfig(TerminatorDataSourceConfig config) {
        this.config = config;
    }

    /**
     * @return
     * @uml.property  name="jdbcProperties"
     */
    public JDBCProperties getJdbcProperties() {
        return jdbcProperties;
    }

    /**
     * @param jdbcProperties
     * @uml.property  name="jdbcProperties"
     */
    public void setJdbcProperties(JDBCProperties jdbcProperties) {
        this.jdbcProperties = jdbcProperties;
    }

    /**
     * 这个方法用于检查必要属性是否注入
     * @return StringUtils.isEmpty(dsName) && StringUtils.isEmpty(dsXmlPath)
     */
    private boolean checkField() {
        return !StringUtils.isEmpty(name) || !StringUtils.isEmpty(path);
    }

    /**
     * 这个方法绝对生成DataSource的策略，只在以下两种情况下重新解析ds.xml文件：
     * 1. config==null且properties==null，即第一次解析
     * 2. ds.xml文件修改时间发生变化
     * @return SimpleDriverDataSource 实例
     * @throws DataSourceDefinationException 未能完成解析则抛出此异常
     */
    protected SimpleDriverDataSource getDataSource() throws DataSourceDefinationException {
        boolean needReparse = false;
        File dsXmlFile = new File(this.path);
        if (!dsXmlFile.exists()) {
            logger.error("指定的ds.xml文件不存在，无法继续进行");
            throw new DataSourceDefinationException("指定的ds.xml文件不存在！");
        }
        if (this.config == null || this.jdbcProperties == null) {
            // 第一次解析
            needReparse = true;
        }
        if (dsXmlFile.lastModified() != this.lastModifiedTime) {
            // 文件修改过了
            needReparse = true;
            this.lastModifiedTime = dsXmlFile.lastModified();
        }
        if (needReparse) {
            // 需要重新解析
            logger.info("ds.xml配置文件发生变更，即将重新解析，本次文件修改时间：" + this.lastModifiedTime);
            this.reparseConfig();
        }
        try {
            return new SimpleDriverDataSource((Driver) Class.forName(config.getDriver()).newInstance(), config.getUrl(), config.getUsername(), config.getPassword());
        } catch (InstantiationException ie) {
            logger.error("指定的驱动程序无法被实例化", ie);
            throw new DataSourceDefinationException("指定的驱动程序无法被实例化", ie);
        } catch (IllegalAccessException iae) {
            logger.error("指定的驱动程序无法被实例化", iae);
            throw new DataSourceDefinationException("指定的驱动程序无法被实例化", iae);
        } catch (ClassNotFoundException cnfe) {
            logger.error("指定的驱动程序无法找到", cnfe);
            throw new DataSourceDefinationException("指定的驱动程序无法找到", cnfe);
        }
    }

    /**
     * 重新解析ds.xml，生成{@link TerminatorDataSourceConfig}以及{@link JDBCProperties}实例
     * @throws DataSourceDefinationException 无法完成解析则抛出此异常
     */
    @SuppressWarnings("unchecked")
    protected void reparseConfig() throws DataSourceDefinationException {
        if (!this.checkField()) {
            throw new DataSourceDefinationException("未定义dsName或dsXmlPath字段，不能完成解析");
        }
        File dsXmlFile = new File(this.path);
        if (!dsXmlFile.exists()) {
            throw new DataSourceDefinationException("指定的ds.xml文件路径不存在");
        }
        // 生成XPath查询语句：/dataSources/dataSource[@name='dsName']
        StringBuilder sb = new StringBuilder();
        sb.append("/dataSources/dataSource[@name=\'");
        sb.append(this.name);
        sb.append("\']");
        SAXBuilder builder = new SAXBuilder();
        try {
            Document document = builder.build(dsXmlFile);
            Element dataSourceElement = (Element) XPath.selectSingleNode(document.getRootElement(), sb.toString());
            String type = dataSourceElement.getAttributeValue(TerminatorDataSource.DATASOURCE_TYPE);
            String driver = dataSourceElement.getChildText(TerminatorDataSource.DRIVER);
            String url = dataSourceElement.getChildText(TerminatorDataSource.URL);
            String username = dataSourceElement.getChildText(TerminatorDataSource.USERNAME);
            String password = dataSourceElement.getChildText(TerminatorDataSource.PASSWORD);
            if (this.config == null) {
                this.config = new TerminatorDataSourceConfig(this.name, driver, url, username, password);
            } else {
                this.config.setDriver(driver);
                this.config.setUrl(url);
                this.config.setUsername(username);
                this.config.setPassword(password);
            }
            jdbcProperties = new JDBCProperties(DBMSType.getDBMSTpye(type));
            // 解析附加属性
            Element propertiesElement = dataSourceElement.getChild(PROPERTIES);
            if (propertiesElement != null) {
                List<Element> extraProperties = propertiesElement.getChildren();
                if (extraProperties != null && extraProperties.size() != 0) {
                    for (Element property : extraProperties) {
                        jdbcProperties.put(property.getAttributeValue(PROPERTIES_NAME), property.getAttributeValue(PROPERTIES_VALUE));
                    }
                }
            }
        } catch (JDOMException e) {
            logger.error("ds.xml文件解析失败", e);
            throw new DataSourceDefinationException("ds.xml文件解析失败", e);
        } catch (IOException e) {
            logger.error("ds.xml文件I/O错误", e);
            throw new DataSourceDefinationException("ds.xml文件I/O错误", e);
        }
    }

    /**
     * 这个类用于保存DataSource的配置信息
     */
    final class TerminatorDataSourceConfig {

        /**
         * @uml.property  name="name"
         */
        private String name;

        /**
         * @uml.property  name="driver"
         */
        private String driver;

        /**
         * @uml.property  name="url"
         */
        private String url;

        /**
         * @uml.property  name="username"
         */
        private String username;

        /**
         * @uml.property  name="password"
         */
        private String password;

        public TerminatorDataSourceConfig(String name, String driver, String url, String username, String password) {
            this.name = name;
            this.driver = driver;
            this.url = url;
            this.username = username;
            this.password = password;
        }

        /**
         * @return
         * @uml.property  name="name"
         */
        public String getName() {
            return name;
        }

        /**
         * @param name
         * @uml.property  name="name"
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return
         * @uml.property  name="driver"
         */
        public String getDriver() {
            return driver;
        }

        /**
         * @param driver
         * @uml.property  name="driver"
         */
        public void setDriver(String driver) {
            this.driver = driver;
        }

        /**
         * @return
         * @uml.property  name="url"
         */
        public String getUrl() {
            return url;
        }

        /**
         * @param url
         * @uml.property  name="url"
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * @return
         * @uml.property  name="username"
         */
        public String getUsername() {
            return username;
        }

        /**
         * @param username
         * @uml.property  name="username"
         */
        public void setUsername(String username) {
            this.username = username;
        }

        /**
         * @return
         * @uml.property  name="password"
         */
        public String getPassword() {
            return password;
        }

        /**
         * @param password
         * @uml.property  name="password"
         */
        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            sb.append("?username=");
            sb.append(username);
            sb.append("&password=");
            sb.append(password);
            return sb.toString();
        }
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        // TODO Auto-generated method stub
        return null;
    }
}
