package kim.daegi.models;

import kim.daegi.ConsoleColors;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class YahooFinance extends Finance {
//    public Finance(String name, String symbol, String status, double previousPrice, double openPrice, double currentPrice, double lowPrice, double highPrice, double diff, double changeRates, double volume, double previousVolume) {

    public YahooFinance(JSONObject jsonObject) {
        super(
                jsonObject.optString("shortName"),
                jsonObject.optString("symbol"),
                jsonObject.optString("marketState"),
                jsonObject.optDouble("regularMarketPreviousClose"),
                jsonObject.optDouble("regularMarketOpen"),
                jsonObject.optDouble("regularMarketPrice"),
                (double)((int)(jsonObject.optDouble("regularMarketDayLow") * 10)) / 10,
                jsonObject.optDouble("regularMarketDayHigh"),
                jsonObject.optDouble("regularMarketChange"),
                jsonObject.optDouble("regularMarketChangePercent"),
                jsonObject.optDouble("regularMarketVolume"),
                0
        );
    }

    public static String getHeaderString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.CYAN_UNDERLINED);
        sb.append(String.format("%-10s", "Symbol"));
        sb.append(String.format("%10s", "Status"));
        sb.append(String.format("%15s", "Volume"));
        sb.append(String.format("%11s", "Price"));
        sb.append(String.format("%13s", "Diff"));
        sb.append(String.format("%10s", "Percent"));
        sb.append(String.format("%16s", "Candle"));
        sb.append(String.format(" %-15s", "Name"));
        sb.append(ConsoleColors.RESET);
        sb.append("\n");

        return sb.toString();
    }

    public static List<YahooFinance> convertTo(JSONObject jsonObject) {
        JSONArray result = jsonObject.getJSONObject("quoteResponse").getJSONArray("result");

        List<YahooFinance> list = new ArrayList<>();

        for (int i = 0; i < result.length(); i++) {
            JSONObject data  = result.getJSONObject(i);
            YahooFinance yahooFinance = new YahooFinance(data);
            list.add(yahooFinance);
        }

        return list;
    }
}
