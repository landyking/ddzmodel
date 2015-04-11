package com.jfreer.game.ddz.core;

import org.junit.Test;

import java.util.InputMismatchException;

import static org.junit.Assert.*;

public class TableTest {
    @Test
    public void testName() throws Exception {
        Integer [] ints=new Integer[]{1,null,2};
        for (Integer one : ints) {
            System.out.println(one +" "+(one instanceof Integer));
        }
    }
}