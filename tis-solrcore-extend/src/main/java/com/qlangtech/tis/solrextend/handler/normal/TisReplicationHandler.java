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
package com.qlangtech.tis.solrextend.handler.normal;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.ReplicationHandler;

/*
 * solr原生的 副本拷贝机制不可控，先将这个废弃掉，有啥问题后面再调整
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TisReplicationHandler extends ReplicationHandler {

    // @Override
    // @SuppressWarnings("all")
    // public void inform(SolrCore core) {
    // 
    // }
    @Override
    public boolean doFetch(SolrParams solrParams, boolean forceReplication) {
        return true;
    }
    // @Override
    // public String getDescription() {
    // 
    // return super.getDescription();
    // }
    // 
    // @Override
    // public NamedList getStatistics() {
    // 
    // return super.getStatistics();
    // }
    // 
    // @Override
    // @SuppressWarnings("all")
    // public void init(NamedList args) {
    // 
    // // super.init(args);
    // }
    // 
    // @Override
    // public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
    // throw new UnsupportedOperationException();
    // }
}
