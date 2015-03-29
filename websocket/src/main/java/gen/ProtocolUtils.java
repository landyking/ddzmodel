package gen;

import gen.request.ReqPlayCard;

import java.util.HashMap;
import java.util.Map;

/**
 * User: landy
 * Date: 15/3/27
 * Time: 下午6:09
 */
public class ProtocolUtils {

    public static final Map<Integer, Class> REQ_MAPPINGS = new HashMap<Integer, Class>();

    static {
        REQ_MAPPINGS.put(10, ReqPlayCard.class);
    }

    public static Class getHandlerName(Class req) throws ClassNotFoundException {
        String simpleName = req.getSimpleName().substring(3) + "Handler";
        return Class.forName("gen.handler." + simpleName);
    }
}
