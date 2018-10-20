package com.s1coder.cat.thread;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 任务并行调用服务
 *
 * @author wangbo 2015-08-17
 * @version 1.1
 */
public class AsynchronousCallUtil {

    private int poolsize;

    private int queueSize;

    private Map<String, Method> modelMap;

    private List<Future<Object>> taskList;

    private ThreadPoolExecutor threadPool;

    /**
     * @param poolsize
     * @param queueSize
     */
    public AsynchronousCallUtil(int poolsize, int queueSize) {
        super();
        this.poolsize = poolsize;
        this.queueSize = queueSize;
        init();
    }

    /**
     * 默认的构造方法,线程池大小直接使用当前系统cpu数量
     */
    public AsynchronousCallUtil() {
        super();
        this.poolsize = Runtime.getRuntime().availableProcessors();
        this.queueSize = 1;
        init();
    }

    private void init() {
        modelMap = new HashMap<String, Method>();
        taskList = new ArrayList<Future<Object>>();
        if (threadPool == null) {
            //synchronized (threadLocal) {
            if (threadPool == null) {
                threadPool = new ThreadPoolExecutor(0, poolsize, 1000L,
                        TimeUnit.MILLISECONDS,
                        new ArrayBlockingQueue<Runnable>(queueSize),
                        new TaskThreadFactory(),
                        new ThreadPoolExecutor.AbortPolicy());
            }
            //threadLocal.notifyAll();
            //	}
        }
    }

    public void setMaxThreadPoolSize(int size) {
        poolsize = size;
    }

    /**
     * 提交并执行任务
     *
     * @param instance 要执行的类实例
     * @param methName 执行方法名
     * @param args     原方法的参数，支持多个参数，
     *                 要调用的方法参数逗号分隔原样填写即可
     * @throws Exception
     */
    public void submit(Object instance, String methName, Object... args)
            throws Exception {
        if (threadPool.isShutdown()) {
            throw new Exception("已经调用过get()方法,线程已终止");
        }

        submitInter(instance, methName, args);
    }

    private void submitInter(Object instance, String methName, Object... args)
            throws Exception {
        Class<? extends Object> className = instance.getClass();
        Method method = modelMap.get(className.getName());
        if (method == null) {
            for (Method item : className.getMethods()) {
                if (item.getName().equalsIgnoreCase(methName)) {
                    method = item;
                    modelMap.put(methName, method);
                    break;
                }
            }
        }
        if (method == null) {
            throw new Exception("Can not find method [" + methName + "] in class:"
                    + className);
        }
        RejectedExecutionException error = null;
        Future<Object> resItem = null;
        do {
            try {
                error = null;
                resItem = threadPool.submit(new AsynchronousTask(method,
                        instance, args));
            } catch (RejectedExecutionException e) {
                synchronized (instance) {
                    instance.wait();
                }
                error = e;
            }
        } while (error != null);

        taskList.add(resItem);
    }

    /**
     * 获得本线程之前提交任务的返回值，List中的值按提交任务的顺序返回
     *
     * @return List<Object>
     * @throws Exception
     */
    public List<Object> get() throws Exception {
        threadPool.shutdown();
        try {
            List<Object> res = getInter();
            return res;
        } catch (Exception e) {
            throw e;
        } finally {
            //threadLocal.remove();
        }
    }

    private List<Object> getInter() throws Exception {
        try {
            List<Object> res = new ArrayList<Object>();
            for (Future<Object> item : taskList) {
                try {
                    res.add(item.get());
                } catch (Exception e) {
                    throw new Exception("thread execute failed : ", e);
                }
            }
            return res;
        } catch (Exception e) {
            throw e;
        } finally {
            taskList.clear();
        }


    }

    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdown();
        }
    }


}

class AsynchronousTask implements Callable<Object> {

    private Method method;

    private Object instance;

    private Object[] args;

    //private HttpServletRequest request;//保存调用线程的request上下文引用 by wangbo 2013-01-29

    public AsynchronousTask(Method method, Object instance, Object... args) {
        super();
        this.method = method;
        this.instance = instance;
        this.args = args;
        //this.request = AppHolder.getRequest();//获取调用线程的上下文
    }

    @Override
    public Object call() throws Exception {
        //AppHolder.setRequest(request);//设置当前上下文
        method.setAccessible(true);
        Object res = method.invoke(instance, args);
        synchronized (instance) {
            instance.notifyAll();
        }
        return res;
    }

}

class TaskThreadFactory implements ThreadFactory {
    static final AtomicInteger poolNumber = new AtomicInteger(1);
    final ThreadGroup group;
    final AtomicInteger threadNumber = new AtomicInteger(1);
    final String namePrefix;

    public TaskThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
                .getThreadGroup();
        namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix
                + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}