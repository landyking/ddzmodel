package com.jfreer.game.websocket.logic;

import org.junit.Test;

public class SubPlayCardTest {
    @Test
    public void test2() throws Exception {

        Class<?> cls = Class.forName("com.jfreer.game.websocket.logic.TestImpl");
        Object obj = cls.newInstance();
        ITest test = (ITest) obj;
        Param t = new Param();
        t.setName("yellow");
        t.setAddress("address.....");
        test.work(t);
    }
}