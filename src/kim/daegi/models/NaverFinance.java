package kim.daegi.models;

import kim.daegi.ConsoleColors;
import kim.daegi.Main;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NaverFinance extends Finance {
    public NaverFinance(JSONObject jsonObject, Map<String, Double> prevVolumeMap) {
        super(
            jsonObject.optString("nm"),
                jsonObject.optString("cd"),
                jsonObject.optString("ms"),
                jsonObject.optDouble("sv"),
                jsonObject.optDouble("ov"),
                jsonObject.optDouble("nv"),
                jsonObject.optDouble("lv"),
                jsonObject.optDouble("hv"),
                jsonObject.optDouble("sv") > jsonObject.optDouble("nv") ? -jsonObject.optDouble("cv") : jsonObject.optDouble("cv"),
                jsonObject.optDouble("sv") > jsonObject.optDouble("nv") ? -jsonObject.optDouble("cr") : jsonObject.optDouble("cr"),
                jsonObject.optDouble("aq"),
                prevVolumeMap.get(jsonObject.optString("cd"))
        );
    }

    public static String getHeaderString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.CYAN_UNDERLINED);
        sb.append(String.format("%-10s", "Symbol"));
        sb.append(String.format("%10s", "Status"));
        sb.append(String.format("%15s", "Volume"));
        sb.append(String.format("%8s", "Rel"));
        sb.append(String.format("%11s", "Price"));
        sb.append(String.format("%13s", "Diff"));
        sb.append(String.format("%10s", "Percent"));
        sb.append(String.format("%16s", "Candle"));
        sb.append(String.format(" %-15s", "Name"));
        sb.append(ConsoleColors.RESET);
        sb.append("\n");

        return sb.toString();
    }

    public static List<NaverFinance> convertTo(JSONObject jsonObject) {
        JSONArray result = jsonObject
                .getJSONObject("result")
                .getJSONArray("areas")
                .getJSONObject(0)
                .getJSONArray("datas");

        List<NaverFinance> list = new ArrayList<>();

        for (int i = 0; i < result.length(); i++) {
            JSONObject data  = result.getJSONObject(i);
            NaverFinance naverFinance = new NaverFinance(data, Main.prevVolumeMap);
            list.add(naverFinance);
        }

        return list;
    }
}
