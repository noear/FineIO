package org.noear.fineio.core;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 资源池
 * */
public class ResourcePool<R> {
    //队列
    private final LinkedBlockingQueue<R> queue;
    //线程状态
    private final ThreadLocal<R> threadLocal = new ThreadLocal<>();

    //资源工厂
    private ResourceFactory<R> factory;

    public ResourcePool(int coreSize, ResourceFactory<R> factory) {
        this.factory = factory;
        this.queue = new LinkedBlockingQueue<>(coreSize);
    }

    /**
     * 申请资源
     * */
    public R apply() {
        try {
            return apply0();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 释放资源
     * */
    public void free() {
        free0();
    }

    /**
     * 清空
     * */
    public void clear(){
        queue.forEach(r->{
            factory.release(r);
        });

        queue.clear();
        threadLocal.set(null);
    }

    private R check(R res) {
        return factory.check(res);
    }

    private R close(R res) {
        return factory.close(res);
    }

    /**
     * 获取资源
     */
    private R apply0() throws InterruptedException {
        R r = threadLocal.get();

        if (r != null) {
            r = check(r);
        }

        if (r == null) {
            if (queue.isEmpty() == false) {
                r = check(queue.take());
            }

            if (r == null) {
                if (null != (r = create0())) {
                    queue.offer(r);
                }
            }

            threadLocal.set(r);
        }

        return r;
    }

    private R create0() {
        try {
            return factory.create();
        } catch (Throwable ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 释放资源
     */
    private void free0() {
        R r = threadLocal.get();

        if (r != null) {
            threadLocal.remove();

            if (null != (r = close(r))) {
                queue.offer(r);
            }
        }
    }
}
