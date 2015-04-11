package com.jfreer.game.websocket.logic;

/**
 * User: landy
 * Date: 15/3/27
 * Time: 下午5:55
 */
public class Param {
    private String name;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Param{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
