package Components;

import org.json.JSONObject;

public class Manejador {
    public Manejador(JSONObject obj) throws Exception {
        switch (obj.getString("component")) {
            case Producto.COMPONENT: Producto.onMessage(obj); break;
            case Tienda.COMPONENT: Tienda.onMessage(obj); break;
            case Usuario.COMPONENT: Usuario.onMessage(obj); break;
        }
    }

}
