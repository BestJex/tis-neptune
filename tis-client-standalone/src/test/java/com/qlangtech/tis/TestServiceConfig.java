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
package com.qlangtech.tis;

import junit.framework.TestCase;
import com.qlangtech.tis.common.config.IServiceConfig;
import com.qlangtech.tis.common.config.ServiceConfig;
import com.qlangtech.tis.common.config.ServiceConfigSupport;
import com.qlangtech.tis.common.zk.OnReconnect;
import com.qlangtech.tis.common.zk.TerminatorZkClient;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TestServiceConfig extends TestCase {

    public void testGetGroupCount() throws Exception {
        // String serviceName,
        // ServiceConfigSupport serviceConfigSupport
        TerminatorZkClient client = TerminatorZkClient.create("10.1.6.65:2181,10.1.6.67:2181,10.1.6.80:2181/tis/cloud", 30000, new OnReconnect() {

            @Override
            public String getReconnectName() {
                return null;
            }

            @Override
            public void onReconnect(TerminatorZkClient zkClient) throws Exception {
            }
        }, false);
        IServiceConfig serviceConfig = new ServiceConfig("search4dfireOrderInfo", client, new ServiceConfigSupport() {

            @Override
            public void onServiceConfigChange(IServiceConfig serviceConfig) {
            }

            @Override
            public IServiceConfig getServiceConfig() {
                return null;
            }
        });
        long current = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            serviceConfig.getGroupNum();
        }
        System.out.println("consume:" + (System.currentTimeMillis() - current));
    }
}
