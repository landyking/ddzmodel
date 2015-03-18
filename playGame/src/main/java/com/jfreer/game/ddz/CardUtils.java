package com.jfreer.game.ddz;

import java.util.Collections;
import java.util.List;

/**
 * User: landy
 * Date: 15/3/16
 * Time: 下午5:31
 */
public class CardUtils {
    /**
     * TODO 是否是合法的牌型
     *
     * @param cards
     * @return
     */
    public static boolean isIegal(byte[] cards) {
        return cards.length == 1;
    }

    /**
     * TODO now是不是比last大
     * @param now
     * @param last
     * @return
     */
    public static boolean isGreater(byte[] now, byte[] last) {
        return now[0] > last[0];
    }

    /**
     * TODO
     * 从handCards中找出比cards大的一组牌。
     * 可能会找到很多组符合条件的牌，使用哪组，根据具体情况定。
     * @param cards
     * @param handCards
     * @return
     */
    public static byte[] getCardsGreaterThan(byte[] cards, List<Byte> handCards) {
        byte old = cards[0];
        for (byte one : handCards) {
            if (one > old) {
                return new byte[]{one};
            }
        }
        return new byte[0];
    }

    /**
     * TODO 找出handCards中最小的一组牌
     * @param handCards
     * @return
     */
    public static byte[] getMinCards(List<Byte> handCards) {
        return new byte[]{Collections.min(handCards)};
    }
}
