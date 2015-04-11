package com.jfreer.game.ddz.exception;

/**
 * User: landy
 * Date: 15/3/31
 * Time: 下午2:32
 */
public class DDZException extends Exception {
    public DDZException() {
    }

    public DDZException(String message) {
        super(message);
    }

    public DDZException(String message, Throwable cause) {
        super(message, cause);
    }

    public DDZException(Throwable cause) {
        super(cause);
    }

}
