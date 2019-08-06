package kim.daegi;

import kim.daegi.models.NaverFinance;
import kim.daegi.models.YahooFinance;
import org.apache.commons.cli.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {
    private static BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));

    private static final String API_ENDPOINT_NAVER ="https://polling.finance.naver.com/api/realtime.nhn?query=SERVICE_ITEM%3A";
    public static Map<String, Double> prevVolumeMap;
//    private static final String API_ENDPOINT_YAHOO ="https://query1.finance.yahoo.com/v7/finance/quote?lang=ko-KR&region=KR&corsDomain=finance.yahoo.com&symbols=";

    public static void main(String[] args) throws Exception {
//        args = new String[]{"-i", "250", "-s", "007700"};

        Options options = new Options();

        Option intervalOption = new Option("i", "interval", true, "Refresh Interval in milliseconds");
        intervalOption.setRequired(true);

        Option symbolsOption = new Option("s", "symbols", true, "KRX stock symbol list by space separated");
        symbolsOption.setArgs(Option.UNLIMITED_VALUES);
        symbolsOption.setRequired(true);

        options.addOption(intervalOption);
        options.addOption(symbolsOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }

        long interval = Long.parseLong(cmd.getOptionValue("interval"));
        String[] symbols = cmd.getOptionValues("symbols");

        new Main().run(interval, symbols);
    }

    private void run(long interval, String[] symbols) { // your business logic goes here...
        final String joinedSymbols = String.join(",", symbols);
        prevVolumeMap = getPreviousVolumeMap(symbols);

        final Thread backgroundThread = new Thread(() -> {
            try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
                while (true) {
                    HttpGet request = new HttpGet(API_ENDPOINT_NAVER + URLEncoder.encode(joinedSymbols, StandardCharsets.UTF_8));
                    HttpResponse httpResponse = httpClient.execute(request);
                    print(EntityUtils.toString(httpResponse.getEntity()));
                    TimeUnit.MILLISECONDS.sleep(interval);
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });

        backgroundThread.start();
    }

    private static void print(String raw) {
        JSONObject jsonObject = new JSONObject(raw);
        StringBuilder sb = new StringBuilder();

//        sb.append(YahooFinance.getHeaderString());
//        List<YahooFinance> yahooFinances = YahooFinance.convertTo(jsonObject);
//        for (YahooFinance yahooFinance : yahooFinances) {
//            sb.append(yahooFinance.toString());
//        }

        sb.append(NaverFinance.getHeaderString());
        List<NaverFinance> naverFinances = NaverFinance.convertTo(jsonObject);
        for (NaverFinance naverFinance : naverFinances) {
            sb.append(naverFinance.toString());
        }

        try {
            bw.write("\033[H\033[2J");
            bw.flush();
            bw.write(sb.toString());
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JSONArray sortByName(JSONArray result) {
        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < result.length(); i++) {
            jsonValues.add(result.getJSONObject(i));
        }

        jsonValues.sort(Comparator.comparing(a -> a.optString("nm")));

        for (JSONObject jsonValue : jsonValues) {
            sortedJsonArray.put(jsonValue);
        }

        return sortedJsonArray;
    }

    private static JSONArray sortByChangeRates(JSONArray jsonArray) {
        List<JSONObject> jsonValues = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            jsonValues.add(jsonArray.getJSONObject(i));
        }

        jsonValues.sort((a, b) -> {
            double valA = a.optDouble("cr");
            valA = a.optDouble("sv")>a.optDouble("nv")?-valA:valA;

            double valB = b.optDouble("cr");
            valB = b.optDouble("sv")>b.optDouble("nv")?-valB:valB;

            return Double.compare(valB, valA);
        });

        return new JSONArray(jsonValues);
    }

    private static Map<String, Double> getPreviousVolumeMap(String[] symbols) {
        final String endpoint = "https://finance.naver.com/item/frgn.nhn?code=";

        Map<String, Double> map = new HashMap<>();

        for(String symbol : symbols) {
            try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
                HttpGet request = new HttpGet(endpoint + symbol);
                HttpResponse result = httpClient.execute(request);
                String html = EntityUtils.toString(result.getEntity());
                Document doc = Jsoup.parse(html);
                Elements select = doc.body().select("div.section.inner_sub > table.type2 > tbody > tr:nth-child(4) > td:nth-child(5)");
                String text = select.text().replace(",","");
                Double volume = Double.parseDouble(text);
                map.put(symbol, volume);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return map;
    }
}
