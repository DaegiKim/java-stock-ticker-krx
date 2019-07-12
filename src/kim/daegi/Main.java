package kim.daegi;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static final String API_ENDPOINT="https://polling.finance.naver.com/api/realtime.nhn?query=SERVICE_ITEM%3A";

    public static void main(String[] args) {
        long interval = Long.parseLong(args[0]);
        String symbols = Arrays.stream(args).skip(1).collect(Collectors.joining(","));

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(API_ENDPOINT + URLEncoder.encode(symbols, StandardCharsets.UTF_8));

            while (true) {
                HttpResponse result = httpClient.execute(request);
                String json = EntityUtils.toString(result.getEntity());
                print(new JSONObject(json));
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void print(JSONObject jsonObject) {
        JSONArray result = jsonObject
                .getJSONObject("result")
                .getJSONArray("areas")
                .getJSONObject(0)
                .getJSONArray("datas");

        result = sort(result);

        StringBuilder sb = new StringBuilder();
        sb.append("\033[H\033[2J");
        sb.append(String.format(ConsoleColors.CYAN_UNDERLINED+"%-7s%8s%11s%10s%15s%16s%7s%21s %-15s\033[0m\n", "Symbol", "Price", "Diff", "Percent", "Volume", "TXN Price", "Status", "Daily Range", "Name"));

        for (int i = 0; i < result.length(); i++) {
            JSONObject data = result.getJSONObject(i);

            String shortName = data.optString("nm");

            String symbol = data.optString("cd");
            String marketState = data.optString("ms");

            double sv = data.optDouble("sv");
            double aq = data.optDouble("aq");
            double aa = data.optDouble("aa");

            double regularMarketPrice = data.optDouble("nv");
            double regularMarketDayHigh = data.optDouble("hv");
            double regularMarketDayLow = data.optDouble("lv");

            double regularMarketChange = data.optDouble("cv");
            regularMarketChange = sv>regularMarketPrice?-regularMarketChange:regularMarketChange;

            double regularMarketChangePercent = data.optDouble("cr");
            regularMarketChangePercent = sv>regularMarketPrice?-regularMarketChangePercent:regularMarketChangePercent;


            String color = regularMarketChange==0?"":regularMarketChange>0?ConsoleColors.GREEN_BOLD_BRIGHT:ConsoleColors.RED_BOLD_BRIGHT;

            sb.append(String.format("%-7s", symbol));

            if(regularMarketDayHigh == regularMarketPrice) {
                sb.append(String.format(ConsoleColors.GREEN_BOLD_BRIGHT+"%,8.0f"+ConsoleColors.RESET, regularMarketPrice));
            } else if(regularMarketDayLow == regularMarketPrice) {
                sb.append(String.format(ConsoleColors.RED_BOLD_BRIGHT+"%,8.0f"+ConsoleColors.RESET, regularMarketPrice));
            } else {
                sb.append(String.format(ConsoleColors.WHITE_BOLD+"%,8.0f"+ConsoleColors.RESET, regularMarketPrice));
            }

            sb.append(String.format(color+"%11s"+ConsoleColors.RESET, String.format("%,.0f", regularMarketChange)+" "+(regularMarketChange>0?"▲":regularMarketChange<0?"▼":"-")));
            sb.append(String.format(color+"%10s"+ConsoleColors.RESET, String.format("(%.2f%%)", regularMarketChangePercent)));
            sb.append(String.format("%,15.0f", aq));
            sb.append(String.format("%,16.0f", aa));
            sb.append(String.format("%7s", marketState));
            sb.append(String.format("%21s", drawPriceRange(regularMarketDayLow, regularMarketDayHigh, regularMarketPrice)));
            sb.append(String.format(" %-15s\n", shortName));
        }
        System.out.print(sb);
    }

    private static String drawPriceRange(double low, double high, double current) {
        int indicator = (int) ((current - low) / (high - low) * 20);
        indicator = indicator==20?indicator-1:indicator;

        String s = "";
        for (int i = 0; i < 20; i++) {
            if(i == indicator) {
                s+=ConsoleColors.YELLOW_BOLD_BRIGHT+"+"+ConsoleColors.RESET;
            } else {
                s+="-";
            }
        }
        s+="";

        return s;
    }

    private static JSONArray sort(JSONArray result) {
        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < result.length(); i++) {
            jsonValues.add(result.getJSONObject(i));
        }

        jsonValues.sort((a, b) -> {
            double valA = a.optDouble("cr");
            valA = a.optDouble("sv")>a.optDouble("nv")?-valA:valA;

            double valB = b.optDouble("cr");
            valB = b.optDouble("sv")>b.optDouble("nv")?-valB:valB;

            return Double.compare(valB, valA);
        });

        for (JSONObject jsonValue : jsonValues) {
            sortedJsonArray.put(jsonValue);
        }

        return sortedJsonArray;
    }
}
