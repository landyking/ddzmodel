package com.jfreer.game.ddz;

/**
 * User: landy
 * Date: 15/3/9
 * Time: 上午11:36
 */
public class Player {
    private int playerId;

    public Player(int playerId) {
        this.playerId = playerId;
    }

    private Consts.CallDealerState callDealerState = Consts.CallDealerState.def;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public Consts.CallDealerState getCallDealerState() {
        return callDealerState;
    }

    public void setCallDealerState(Consts.CallDealerState callDealerState) {
        this.callDealerState = callDealerState;
    }

    public void setHandCards(byte[] card) {

    }

    @Override
    public String toString() {
        return "Player:"+ playerId;
    }

    public void turnCallDealer() {

    }
}
