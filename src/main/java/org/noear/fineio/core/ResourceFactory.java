package org.noear.fineio.core;

/**
 * 资源工厂
 * */
public interface ResourceFactory<R> {
    /**
     * 创建资源
     * */
    R create() throws Throwable;

    /**
     * 检查资源
     * */
    default R check(R r){
        return r;
    }

    /**
     * 释放自由
     * */
    default R free(R r){
        return r;
    }

    /**
     * 关闭
     * */
    void close(R r);
}
