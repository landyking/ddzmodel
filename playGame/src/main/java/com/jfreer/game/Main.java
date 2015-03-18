package com.jfreer.game;

import com.jfreer.game.ddz.Ids;
import com.jfreer.game.ddz.Player;
import com.jfreer.game.ddz.RobotPlayer;
import com.jfreer.game.ddz.Table;

import java.util.Collections;
import java.util.LinkedList;

/**
 * User: landy
 * Date: 15/3/13
 * Time: 上午11:38
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Table table = new Table();
        Player player1 = new RobotPlayer(Ids.getPlayerId());
        Player player2 = new RobotPlayer(Ids.getPlayerId());
        Player player3 = new RobotPlayer(Ids.getPlayerId());

        while (true) {
            System.out.println("#######################################################");
            System.out.println("#######################################################");
            table.reset();
            player1.getHandCards().clear();
            player2.getHandCards().clear();
            player3.getHandCards().clear();
            LinkedList<Byte> cards = getShuffleCards();
            while (!cards.isEmpty()) {
                player1.getHandCards().add(cards.remove());
                player2.getHandCards().add(cards.remove());
                player3.getHandCards().add(cards.remove());
            }
            Collections.sort(player1.getHandCards());
            Collections.sort(player2.getHandCards());
            Collections.sort(player3.getHandCards());
            System.out.println(player1 + player1.getHandCards().toString());
            System.out.println(player2 + player2.getHandCards().toString());
            System.out.println(player3 + player3.getHandCards().toString());
            table.addPlayer(player1);
            table.addPlayer(player2);
            table.addPlayer(player3);

            table.startPlaying();
        }
    }

    private static LinkedList<Byte> getShuffleCards() {
        LinkedList<Byte> cards = new LinkedList<Byte>();
        int total=3*3;
        for (byte one = 1; one <= total; one++) {
            cards.add(one);
        }
        Collections.shuffle(cards);
        return cards;
    }
}
