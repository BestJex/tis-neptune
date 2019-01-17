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
package com.qlangtech.tis.manage.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.dispatcher.StrutsRequestWrapper;
import com.qlangtech.tis.manage.biz.dal.pojo.Department;
import com.qlangtech.tis.manage.biz.dal.pojo.UsrDptRelation;
import com.qlangtech.tis.manage.biz.dal.pojo.UsrDptRelationCriteria;
import com.qlangtech.tis.manage.common.apps.IAppsFetcher;
import com.qlangtech.tis.manage.common.apps.TerminatorAdminAppsFetcher;
import com.qlangtech.tis.runtime.module.action.LoginAction;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class UserUtils {

    public static final String USER_TOKEN = "user_token";

    // public static final TUser getUser(HttpServletRequest request,
    // RunContext runContext) {
    // return UserUtils.getUser(DefaultFilter.getReqeust(), runContext);
    // }
    public static final IAppsFetcher getAppsFetcher(HttpServletRequest request, RunContext runContext) {
        return getUser(request, runContext).getAppsFetcher();
    }

    public static final String USER_TOKEN_SESSION = UserUtils.class.getName() + "user";

    public static final IUser getUser(final HttpServletRequest r, RunContext runContext) {
        final TerminatorHttpServletRequestWrapper request = (TerminatorHttpServletRequestWrapper) (((StrutsRequestWrapper) r).getRequest());
        HttpSession session = request.getSession();
        TUser result = null;
        try {
            if ((result = getUserFromCache(request)) == null) {
                Cookie userCookie = request.getCookie(UserUtils.USER_TOKEN);
                if (userCookie != null) {
                    UsrDptRelationCriteria query = new UsrDptRelationCriteria();
                    query.createCriteria().andUserNameEqualTo(LoginAction.getDcodeUserName(userCookie.getValue()));
                    for (UsrDptRelation usr : runContext.getUsrDptRelationDAO().selectByExample(query)) {
                        result = new TUser(usr, runContext);
                        session.setAttribute(USER_TOKEN_SESSION, result);
                        return result;
                    }
                } else {
                    return NOT_LOGIN_USER;
                }
            // SimpleSSOUser user = SimpleUserUtil.findUser(request);
            // result = new TUser(user.getEmpId(),
            // StringUtils.defaultIfEmpty(
            // user.getNickNameCn(), user.getLastName()), runContext);
            // result.setDepartment(user.getDepDesc());
            // result = new TUser("18097", "baisui", runContext);
            // result.setDepartment("manage");
            // 
            // // 阿里巴巴全局departmentId
            // result.setDepartmentid(123);
            // result.setWangwang("百岁");
            // result.setEmail("bvaisui@taobao.com");
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static final TUser getUserFromCache(HttpServletRequest request) {
        return (TUser) request.getSession().getAttribute(USER_TOKEN_SESSION);
    }

    private static TUser DEFAULT_SUPER_USER;

    private static final TUser getMockUser(HttpServletRequest request, RunContext runContext) {
        if (DEFAULT_SUPER_USER == null) {
            UsrDptRelation usr = new UsrDptRelation();
            usr.setUsrId("9999");
            usr.setUserName("admin");
            DEFAULT_SUPER_USER = new TUser(usr, runContext, new SuperUserFetcher(runContext));
            DEFAULT_SUPER_USER.setDepartmentid(8);
            DEFAULT_SUPER_USER.setDepartment("管理");
        }
        return DEFAULT_SUPER_USER;
    }

    private static TUser NOT_LOGIN_USER;

    static {
        UsrDptRelation usr = new UsrDptRelation();
        usr.setUsrId("-1");
        usr.setUserName("none");
        NOT_LOGIN_USER = new TUser(usr, null, new SuperUserFetcher(null)) {

            @Override
            public boolean hasLogin() {
                return false;
            }
        };
        NOT_LOGIN_USER.setDepartmentid(-1);
        NOT_LOGIN_USER.setDepartment("none");
    }

    private static class SuperUserFetcher extends TerminatorAdminAppsFetcher {

        public SuperUserFetcher(RunContext context) {
            super(null, null, context);
        }

        @Override
        protected void processDepartment(Department department, RunContext context) {
        // super.processDepartment(department, context);
        }
    }
}
