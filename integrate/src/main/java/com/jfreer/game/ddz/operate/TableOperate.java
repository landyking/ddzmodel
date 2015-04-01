package com.jfreer.game.ddz.operate;

import com.jfreer.game.ddz.Player;

/**
 * Created by landy on 2015/3/8.
 */
public class TableOperate extends IOperate {
    protected Player player;
    private Integer destTableId;
    private byte orderNo;

    public byte getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(byte orderNo) {
        this.orderNo = orderNo;
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
