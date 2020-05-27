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
     * 打开资源（可以打开时，进行检测）
     * */
    default R open(R r){
        return r;
    }

    /**
     * 关闭资料
     * */
    default R close(R r){
        return r;
    }
}
