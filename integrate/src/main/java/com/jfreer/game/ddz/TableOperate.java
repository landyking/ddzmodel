package com.jfreer.game.ddz;

/**
 * Created by landy on 2015/3/8.
 */
public class TableOperate {
    protected Player player;
    private Integer destTableId;
    private Runnable callback;
    private byte orderNo;

    public byte getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(byte orderNo) {
        this.orderNo = orderNo;
    }
    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    public Runnable getCallback() {
        return callback;
    }

    public Integer getDestTableId() {
        return destTableId;
    }

    public void setDestTableId(Integer destTableId) {
        this.destTableId = destTableId;
    }
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
