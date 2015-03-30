package com.jfreer.game.ddz;

/**
 * User: landy
 * Date: 15/3/8
 * Time: 下午4:41
 */
public interface Callback<T> {
    void doWork(T data) throws Exception;
}
