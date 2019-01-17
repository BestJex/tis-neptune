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
package com.qlangtech.tis.fullbuild.taskflow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import com.qlangtech.tis.fullbuild.taskflow.odps.ODPSTask;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class Concurrent extends AdapterTask {

    private List<ITask> tasks = new ArrayList<ITask>();

    public void addTask(ODPSTask task) {
        this.tasks.add(task);
    }

    public void addTask(List<ITask> subtask) {
        this.tasks.addAll(subtask);
    }

    @Override
    public void exexute() {
        final CountDownLatch countdown = new CountDownLatch(tasks.size());
        for (final ITask task : tasks) {
            ForTask.executor.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        task.exexute();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        countdown.countDown();
                    }
                }
            });
        }
        try {
            countdown.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void executeSql(String taskname, String sql) {
        throw new UnsupportedOperationException();
    }
}
