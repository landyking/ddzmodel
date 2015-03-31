package com.jfreer.game.ddz;

import org.junit.Test;

import java.util.Arrays;

public class CardUtilsTest {

    @Test
    public void testGetTableCards() throws Exception {
        byte[][] tableCards = CardUtils.getTableCards(null);
        for (byte[] one : tableCards) {
            System.out.println(Arrays.toString(one));
        }
    }
}