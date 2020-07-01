package recursion;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author: xiaochuan
 * @Date: 2020/7/01 16:52
 * @Description: 通过扁平化的字段获取json中的值
 */
public class FlatUtil {

    /**
     * 通过扁平化的字段获取json中的值
     * @param json 它可以是一个list也可以是一个map以及它们的子类们，可以抽象的理解为json
     * @param flatField 扁平化的字段
     * @return 值
     */
    public static Object get(Object json, String flatField) {
        checkJson(json);
        String[] flatFieldArr = checkFlatField(flatField);
        return get(json, 0, flatFieldArr);
    }

    private static void checkJson(Object json) {
        if (json == null) {
            throw new RuntimeException("待操作的json对象不能为空");
        }
        if (!(json instanceof Map) && !(json instanceof Collection)) {
            throw new RuntimeException("仅支持Collection或Map及其它们的子类");
        }
    }

    private static String[] checkFlatField(String flatField) {
        if (flatField == null || flatField.length() <= 0) {
            throw new RuntimeException("flatField不能为空");
        }
        String[] flatFieldArr = flatField.split("\\.");
        for (String ff : flatFieldArr) {
            if (ff == null || ff.length() <= 0) {
                throw new RuntimeException("flatField格式不正确，仅支持'field1.field2.field3...'扁平化字段格式");
            }
        }
        return flatFieldArr;
    }

    private static Object get(Object json, int point, String[] flatFieldArr) {
        if (json == null) {
            return null;
        }
        if (point == flatFieldArr.length) {
            return json;
        }
        String key = flatFieldArr[point];
        if (json instanceof Map) {
            Map jsonMap = (Map) json;
            Object childJson = jsonMap.get(key);
            return get(childJson, ++point, flatFieldArr);
        } else if (json instanceof Collection) {
            Collection jsonArr = (Collection) json;
            List<Object> result = new ArrayList<>();
            for (Object obj : jsonArr) {
                Object childJson = get(obj, point, flatFieldArr);
                if (childJson != null) {
                    if (childJson instanceof Collection) {
                        result.addAll((Collection) childJson);
                    } else {
                        result.add(childJson);
                    }
                }
            }
            if (result.size() <= 0) {
                return null;
            }
            return result;
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        String json = "{\"bool\":{\"must\":[{\"term\":{\"resCategory\":{\"value\":\"port_info\"}}},{\"term\":{\"port\":{\"value\":\"1024\"}}}]}}";
        JSONObject jsonObject = JSON.parseObject(json);
        System.out.println(FlatUtil.get(jsonObject, "bool.must"));
        System.out.println(FlatUtil.get(jsonObject, "bool.must.term.resCategory.value"));
    }
}
