package com.jfreer.game;

import com.jfreer.game.ddz.Ids;
import com.jfreer.game.ddz.Player;
import com.jfreer.game.ddz.Table;

/**
 * User: landy
 * Date: 15/3/10
 * Time: 上午11:30
 */
public class Main {
    public static void main(String[] args) {
        Table table = new Table();
        Player player1 = new Player(Ids.getPlayerId());
        table.joinPlayer(player1);

        Player player2 = new Player(Ids.getPlayerId());
        table.joinPlayer(player2);

        Player player3 = new Player(Ids.getPlayerId());
        table.joinPlayer(player3);

        table.restartGame();
    }
}
