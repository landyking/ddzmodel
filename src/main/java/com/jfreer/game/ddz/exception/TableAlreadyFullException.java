package com.jfreer.game.ddz.exception;

/**
 * User: landy
 * Date: 15/3/31
 * Time: 下午2:47
 */
public class TableAlreadyFullException extends DDZException {
    private Integer table;

    public TableAlreadyFullException(Integer table) {
        super("table " + table + " is already full!");
        this.table = table;
    }

    public Integer getTable() {
        return table;
    }
}
