package common.utils;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liubin on 15-4-24.
 */
public class JsonResult {

    private Boolean result;

    private Object data;

    private String message;

    public JsonResult(Boolean result) {
        this(result, null, null);
    }

    public JsonResult(Boolean result, String message) {
        this(result, message, null);
    }

    public JsonResult(Boolean result, String message, Object data) {
        this.result = result;
        this.data = data;
        this.message = message;
    }

    public JsonNode toNode() {
        Map<String, Object> map = new HashMap<>();
        map.put("result", result);
        map.put("data", data);
        map.put("message", message);

        return JsonUtils.object2Node(map);
    }

    public static JsonResult fromJson(String json) {
        Map map = JsonUtils.json2Object(json, Map.class);
        JsonResult jsonResult = new JsonResult((Boolean)map.get("result"), (String)map.get("message"), map.get("data"));
        return jsonResult;
    }


    public Boolean getResult() {
        return result;
    }

    public Object getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

}
