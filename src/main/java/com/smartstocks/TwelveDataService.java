package com.smartstock;

import org.json.JSONArray;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.util.*;
import java.time.LocalDate;


@Service
public class TwelveDataService {

    private final String API_KEY = "6282452c66574589b6df066dab8874ea";  // Replace with your key
    private final String BASE_URL = "https://api.twelvedata.com";

    public Map<String, String> getLatestStockPrice(String symbol) {
        RestTemplate restTemplate = new RestTemplate();
        String url = BASE_URL + "/quote?symbol=" + symbol + "&apikey=" + API_KEY;

        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = new JSONObject(response);

        if (json.has("code")) {
            throw new RuntimeException("Error from Twelve Data: " + json.toString());
        }

        Map<String, String> result = new HashMap<>();
        result.put("symbol", json.getString("symbol"));
        result.put("time", json.getString("datetime"));
        result.put("open", json.getString("open"));
        result.put("high", json.getString("high"));
        result.put("low", json.getString("low"));
        result.put("close", json.getString("close"));
        result.put("volume", json.optString("volume", "N/A"));

        return result;
    }
    public List<Map<String, String>> getIntradayHistory(String symbol) {
        RestTemplate restTemplate = new RestTemplate();
        String url = BASE_URL + "/time_series?symbol=" + symbol + "&interval=5min&outputsize=30&apikey=" + API_KEY;

        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = new JSONObject(response);

        if (!json.has("values")) {
            throw new RuntimeException("No 'values' found in Twelve Data response: " + json.toString());
        }

        List<Map<String, String>> history = new ArrayList<>();
        for (Object obj : json.getJSONArray("values")) {
            JSONObject entry = (JSONObject) obj;
            Map<String, String> point = new HashMap<>();
            point.put("datetime", entry.getString("datetime"));
            point.put("close", entry.getString("close"));
            history.add(point);
        }

        // Sort oldest to newest
        history.sort(Comparator.comparing(m -> m.get("datetime")));

        return history;
    }
    public List<Map<String, String>> getHistoricalData(String symbol, String interval, int outputSize) {
        RestTemplate restTemplate = new RestTemplate();
        String url = BASE_URL + "/time_series?symbol=" + symbol + "&interval=" + interval + "&outputsize=" + outputSize + "&apikey=" + API_KEY;

        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = new JSONObject(response);

        if (!json.has("values")) {
            throw new RuntimeException("No 'values' found in Twelve Data response: " + json.toString());
        }

        List<Map<String, String>> history = new ArrayList<>();
        for (Object obj : json.getJSONArray("values")) {
            JSONObject entry = (JSONObject) obj;
            Map<String, String> point = new HashMap<>();
            point.put("datetime", entry.getString("datetime"));
            point.put("close", entry.getString("close"));
            history.add(point);
        }

        // Sort from oldest to newest
        history.sort(Comparator.comparing(m -> m.get("datetime")));
        return history;
    }

    public Map<String, String> getPerformanceReturns(String symbol) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> periods = Map.of(
                "ytd", LocalDate.now().withDayOfYear(1).toString(),
                "1y", LocalDate.now().minusYears(1).toString(),
                "3y", LocalDate.now().minusYears(3).toString(),
                "5y", LocalDate.now().minusYears(5).toString()
        );

        Map<String, String> returns = new LinkedHashMap<>();

        try {
            // Get current price from /quote
            String latestUrl = BASE_URL + "/quote?symbol=" + symbol + "&apikey=" + API_KEY;
            String latestResponse = restTemplate.getForObject(latestUrl, String.class);
            double currentPrice = new JSONObject(latestResponse).getDouble("close");

            for (Map.Entry<String, String> entry : periods.entrySet()) {
                String period = entry.getKey();
                String startDate = entry.getValue();

                try {
                    // Request a range (from historical start to today)
                    String historicalUrl = BASE_URL + "/time_series?symbol=" + symbol +
                            "&interval=1day" +
                            "&start_date=" + startDate +
                            "&end_date=" + LocalDate.now() +
                            "&order=asc" +
                            "&apikey=" + API_KEY;

                    String response = restTemplate.getForObject(historicalUrl, String.class);
                    JSONObject json = new JSONObject(response);

                    if (!json.has("values")) {
                        returns.put(period, "N/A");
                        continue;
                    }

                    JSONArray values = json.getJSONArray("values");

                    if (values.length() == 0) {
                        returns.put(period, "N/A");
                        continue;
                    }

                    double pastPrice = values.getJSONObject(0).getDouble("close");

                    double percentChange = ((currentPrice - pastPrice) / pastPrice) * 100;
                    returns.put(period, String.format("%.2f", percentChange));
                } catch (Exception e) {
                    returns.put(period, "N/A");
                }

            }

        } catch (Exception e) {
            for (String key : periods.keySet()) {
                returns.put(key, "N/A");
            }
        }

        return returns;
    }

    public Map<String, String> getStockStatistics(String symbol) {
        String API_KEY = "ag9USHAapQsKuR73t6Z4aJ3cu24izxs0"; // Replace with your real key
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> stats = new LinkedHashMap<>();

        try {
            // 1. Profile Endpoint
            String profileUrl = "https://financialmodelingprep.com/api/v3/profile/" + symbol + "?apikey=" + API_KEY;
            JSONArray profileArr = new JSONArray(restTemplate.getForObject(profileUrl, String.class));
            if (profileArr.length() > 0) {
                JSONObject profile = profileArr.getJSONObject(0);

                if (profile.has("mktCap") && !profile.isNull("mktCap"))
                    stats.put("Market Cap", profile.get("mktCap").toString());
                else stats.put("Market Cap", "Data not available");

                if (profile.has("enterpriseValue") && !profile.isNull("enterpriseValue"))
                    stats.put("Enterprise Value", profile.get("enterpriseValue").toString());
                else stats.put("Enterprise Value", "Data not available");

                if (profile.has("priceToSalesRatioTTM") && !profile.isNull("priceToSalesRatioTTM"))
                    stats.put("Price/Sales (ttm)", profile.get("priceToSalesRatioTTM").toString());
                else stats.put("Price/Sales (ttm)", "Data not available");

                if (profile.has("priceToBookRatio") && !profile.isNull("priceToBookRatio"))
                    stats.put("Price/Book (mrq)", profile.get("priceToBookRatio").toString());
                else stats.put("Price/Book (mrq)", "Data not available");
            }

            // 2. Ratios TTM
            String ratioUrl = "https://financialmodelingprep.com/api/v3/ratios-ttm/" + symbol + "?apikey=" + API_KEY;
            JSONArray ratioArr = new JSONArray(restTemplate.getForObject(ratioUrl, String.class));
            if (ratioArr.length() > 0) {
                JSONObject ratios = ratioArr.getJSONObject(0);

                stats.put("Profit Margin", String.format("%.2f%%", ratios.optDouble("netProfitMarginTTM", 0) * 100));
                stats.put("Return on Assets (ttm)", String.format("%.2f%%", ratios.optDouble("returnOnAssetsTTM", 0) * 100));
                stats.put("Return on Equity (ttm)", String.format("%.2f%%", ratios.optDouble("returnOnEquityTTM", 0) * 100));
            }

            // 3. Key Metrics TTM
            String keyMetricsUrl = "https://financialmodelingprep.com/api/v3/key-metrics-ttm/" + symbol + "?apikey=" + API_KEY;
            JSONArray metricsArr = new JSONArray(restTemplate.getForObject(keyMetricsUrl, String.class));
            if (metricsArr.length() > 0) {
                JSONObject metrics = metricsArr.getJSONObject(0);

                if (metrics.has("revenuePerShareTTM") && !metrics.isNull("revenuePerShareTTM"))
                    stats.put("Revenue (ttm)", metrics.get("revenuePerShareTTM").toString());
                else stats.put("Revenue (ttm)", "Data not available");

                if (metrics.has("netIncomePerShareTTM") && !metrics.isNull("netIncomePerShareTTM"))
                    stats.put("Net Income (ttm)", metrics.get("netIncomePerShareTTM").toString());
                else stats.put("Net Income (ttm)", "Data not available");

                if (metrics.has("freeCashFlowTTM") && !metrics.isNull("freeCashFlowTTM"))
                    stats.put("Free Cash Flow (ttm)", metrics.get("freeCashFlowTTM").toString());
                else stats.put("Free Cash Flow (ttm)", "Data not available");

                stats.put("Total Debt/Equity (mrq)", String.format("%.2f%%", metrics.optDouble("debtEquityRatioTTM", 0) * 100));
            }

        } catch (Exception e) {
            stats.put("Error", "Failed to fetch statistics: " + e.getMessage());
        }

        return stats;
    }








}
