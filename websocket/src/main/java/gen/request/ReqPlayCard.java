package gen.request;

import com.jfreer.game.websocket.IReq;

/**
 * User: landy
 * Date: 15/3/27
 * Time: 下午5:59
 */
public class ReqPlayCard extends IReq {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ReqPlayCard{" +
                "name='" + name + '\'' +
                '}';
    }
}
