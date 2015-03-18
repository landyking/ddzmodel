package com.jfreer.game.ddz;

import java.util.Collections;
import java.util.List;

/**
 * User: landy
 * Date: 15/3/16
 * Time: 下午5:31
 */
public class CardChecker {
    public static boolean isIegal(byte[] cards) {
        return cards.length == 1;
    }

    public static boolean isGreater(byte[] now, byte[] last) {
        return now[0] > last[0];
    }

    public static byte[] getCardsGreaterThan(byte[] cards, List<Byte> handCards) {
        byte old = cards[0];
        for (byte one : handCards) {
            if (one > old) {
                return new byte[]{one};
            }
        }
        return new byte[0];
    }

    public static byte[] getMinCards(List<Byte> handCards) {
        return new byte[]{Collections.min(handCards)};
    }
}
