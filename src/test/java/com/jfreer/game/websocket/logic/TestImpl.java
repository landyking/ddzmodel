package com.jfreer.game.websocket.logic;

/**
 * User: landy
 * Date: 15/3/27
 * Time: 下午5:55
 */
public class TestImpl implements ITest<Param> {
    @Override
    public void work(Param param) {
        System.out.println(param.toString());
    }
}
