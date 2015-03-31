package com.jfreer.game.ddz;

import com.jfreer.game.ddz.core.Table;

import java.util.*;

/**
 * User: landy
 * Date: 15/3/16
 * Time: 下午5:31
 */
public class CardUtils {

    public static final int TOTAL_CARD_COUNT = 24;
    public static final int BELOW_CARD_COUNT = 3;
    public static final int PER_CARD_COUNT = (TOTAL_CARD_COUNT - BELOW_CARD_COUNT) / 3;

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
     *
     * @param now
     * @param last
     * @return
     */
    public static boolean isGreater(byte[] now, byte[] last) {
        return now[0] / 4 > last[0] / 4;
    }

    /**
     * TODO
     * 从handCards中找出比cards大的一组牌。
     * 可能会找到很多组符合条件的牌，使用哪组，根据具体情况定。
     *
     * @param cards
     * @param handCards
     * @return
     */
    public static byte[] getCardsGreaterThan(byte[] cards, List<Byte> handCards) {
        byte old = cards[0];
        for (byte one : handCards) {
            if (one / 4 > old / 4) {
                return new byte[]{one};
            }
        }
        return new byte[0];
    }

    /**
     * TODO 找出handCards中最小的一组牌
     *
     * @param handCards
     * @return
     */
    public static byte[] getMinCards(List<Byte> handCards) {
        return new byte[]{Collections.min(handCards)};
    }

    public static byte[][] getTableCards(Table table) {
        LinkedList<Byte> all = new LinkedList<Byte>();
        for (int i = 0; i < TOTAL_CARD_COUNT; i++) {
            all.add((byte) i);
        }
        Collections.shuffle(all);
        Deque<Byte> stack = all;
        byte[][] result = new byte[][]{new byte[PER_CARD_COUNT], new byte[PER_CARD_COUNT], new byte[PER_CARD_COUNT], new byte[BELOW_CARD_COUNT]};
        int i = 0;
        while (stack.size() > BELOW_CARD_COUNT) {
            result[0][i] = stack.pop();
            result[1][i] = stack.pop();
            result[2][i] = stack.pop();
            i++;
        }
        i = 0;
        result[3][i++] = stack.pop();
        result[3][i++] = stack.pop();
        result[3][i++] = stack.pop();
        return result;
    }
}
