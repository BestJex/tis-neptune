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
package com.qlangtech.tis.trigger.biz.dal.dao;

import com.qlangtech.tis.trigger.biz.dal.pojo.TaskExecLog;
import com.qlangtech.tis.trigger.biz.dal.pojo.TaskExecLogCriteria;
import java.util.List;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public interface ITaskExecLogDAO {

    int countByExample(TaskExecLogCriteria example);

    int countFromWriteDB(TaskExecLogCriteria example);

    int deleteByExample(TaskExecLogCriteria criteria);

    int deleteByPrimaryKey(Long execLogId);

    Long insert(TaskExecLog record);

    Long insertSelective(TaskExecLog record);

    List<TaskExecLog> selectByExampleWithBLOBs(TaskExecLogCriteria example);

    List<TaskExecLog> selectByExampleWithoutBLOBs(TaskExecLogCriteria criteria);

    List<TaskExecLog> selectByExampleWithoutBLOBs(TaskExecLogCriteria example, int page, int pageSize);

    TaskExecLog selectByPrimaryKey(Long execLogId);

    int updateByExampleSelective(TaskExecLog record, TaskExecLogCriteria example);

    int updateByExampleWithBLOBs(TaskExecLog record, TaskExecLogCriteria example);

    int updateByExampleWithoutBLOBs(TaskExecLog record, TaskExecLogCriteria example);

    TaskExecLog loadFromWriteDB(Long execLogId);
}
