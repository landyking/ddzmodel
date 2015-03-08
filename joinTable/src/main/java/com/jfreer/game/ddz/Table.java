package com.jfreer.game.ddz;

/**
 * Created by landy on 2015/3/7.
 */
public class Table {
    private Integer tableId;

    public boolean isFull() {
        return false;
    }

    public void joinTable(Player player) {

    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }
}
