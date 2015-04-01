package com.jfreer.game.ddz.thread;

import org.junit.Test;

import java.util.Comparator;
import java.util.PriorityQueue;

public class DDZExecutorTest {
    @Test
    public void testPiro() throws Exception {
        PriorityQueue<Integer> queue = new PriorityQueue<Integer>(20, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1-o2;
            }
        });
        queue.add(22);
        queue.add(2333);
        queue.add(21);
        queue.add(33);
        System.out.println(queue);
        Integer poll = queue.poll();
        System.out.println(poll);
        System.out.println(queue);
    }
}