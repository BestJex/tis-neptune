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
package com.qlangtech.tis.runtime.module.screen;

import com.alibaba.citrus.turbine.Context;
import com.qlangtech.tis.manage.common.Config;

/*
 * 查询响应时间查看
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public abstract class Queryresponse extends BasicScreen {

    // private static final Pattern DATASOURCE_PATTERN =
    // Pattern.compile("/home/admin/terminator-report/2012-02-12/search4ark/")
    /**
     */
    private static final long serialVersionUID = 1L;

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taobao.terminator.runtime.module.screen.BasicScreen#execute(com.alibaba
	 * .citrus.turbine.Context)
	 */
    @Override
    public // @Func("query_react_time_view") 不需要有权限控制
    void execute(Context context) throws Exception {
        this.enableChangeDomain(context);
        context.put("groupAdapterList", createServerGroupAdapterList(false));
        context.put("appdomain", this.getAppDomain());
        context.put("responseTimeHost", Config.getResponseTimeHost(this.getAppDomain().getRunEnvironment()));
    }
}
