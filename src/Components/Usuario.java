package Components;

import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import Conexion.ConexionPostgres;

public class Usuario {
    public static final String COMPONENT = "usuario";

    public static void onMessage(JSONObject obj) throws Exception {
        switch (obj.getString("type")) {
            case "getAll":
                getAll(obj);
                break;
            case "registro":
                registro(obj);
                break;
            case "editar":
                editar(obj);
                break;
            case "delete":
                delete(obj);
                break;
            case "getByKey":
                getByKey(obj);
                break;
            case "login":
                login(obj);
                break;
        }
    }

    public static void login(JSONObject obj) throws JSONException {
        try {
            String consulta = "select get_by_key('" + COMPONENT +"','usuario','"+obj.getString("usuario")+"') as json";
            JSONObject data = ConexionPostgres.ejecutarConsultaObject(consulta);
            if(data != null && data.has("key")){
                if(data.getString("contrasena").equals(obj.getString("contrasena"))){
                    data.remove("contrasena");
                    data.remove("key");
                    data.remove("key_usuario");
                    obj.remove("usuario");
                    obj.remove("contrasena");
                    data.put("token", UUID.randomUUID());
                    obj.put("data", data);
                    obj.put("estado", "exito");
                }else{
                    obj.put("error", "Contraseña erronea");
                    obj.put("estado", "error");
                }
            }else{
                obj.put("error", "Usuario erroneo");
                obj.put("estado", "error");
            }
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getByKey(JSONObject obj) throws JSONException {
        try {
            if(!obj.has("token") || obj.isNull("token") || obj.getString("token").length()>0){
                obj.remove("data");
                obj.put("estado", "error");
                obj.put("error", "No tiene session activa");
                return;
            }
            String consulta = "select get_by_key('" + COMPONENT +"','"+obj.getString("campo")+"','"+obj.getString("key")+"') as json";
            JSONObject data = ConexionPostgres.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getAll(JSONObject obj) throws JSONException {
        try {
            if(!obj.has("token") || obj.isNull("token") || obj.getString("token").length()>0){
                obj.remove("data");
                obj.put("estado", "error");
                obj.put("error", "No tiene session activa");
                return;
            }
            String consulta = "select get_all('" + COMPONENT + "') as json";
            JSONObject data = ConexionPostgres.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void editar(JSONObject obj) throws JSONException {
        try {
            if(!obj.has("token") || obj.isNull("token") || obj.getString("token").length()>0){
                obj.remove("data");
                obj.put("estado", "error");
                obj.put("error", "No tiene session activa");
                return;
            }
            JSONObject data = obj.getJSONObject("data");
            ConexionPostgres.editObject("areas", data);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void delete(JSONObject obj) throws JSONException {
        try {
            if(!obj.has("token") || obj.isNull("token") || obj.getString("token").length()>0){
                obj.remove("data");
                obj.put("estado", "error");
                obj.put("error", "No tiene session activa");
                return;
            }
            ConexionPostgres.anular(COMPONENT, obj.getString("key"));
            obj.put("data", "Se Elimino Correctamente");
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void registro(JSONObject obj) throws JSONException {
        try {
            if(!obj.has("token") || obj.isNull("token") || obj.getString("token").length()>0){
                obj.remove("data");
                obj.put("estado", "error");
                obj.put("error", "No tiene session activa");
                return;
            }

            JSONObject data = obj.getJSONObject("data");
            data.put("key", UUID.randomUUID().toString());
            data.put("estado", 1);
            data.put("fecha_on", "now()");
            ConexionPostgres.insertArray(COMPONENT, new JSONArray().put(data));
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
