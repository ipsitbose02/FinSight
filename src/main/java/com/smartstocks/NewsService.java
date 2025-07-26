package com.smartstock;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.*;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.*;

@Service
public class NewsService {
    private final String API_KEY = "08fc9c434c534a4d9b6ccf6d5c26f67f";  // <-- Replace with your actual key
    private final String BASE_URL = "https://newsapi.org/v2/everything";

    public List<Map<String, String>> getStockNews(String symbol) {
        RestTemplate restTemplate = new RestTemplate();
        String url = BASE_URL + "?q=" + symbol + "&language=en&sortBy=publishedAt&pageSize=10&apiKey=" + API_KEY;

        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = new JSONObject(response);

        List<Map<String, String>> newsList = new ArrayList<>();
        JSONArray articles = json.getJSONArray("articles");

        for (int i = 0; i < articles.length(); i++) {
            JSONObject item = articles.getJSONObject(i);
            Map<String, String> news = new HashMap<>();
            news.put("headline", item.getString("title"));
            news.put("url", item.getString("url"));
            news.put("source", item.getJSONObject("source").getString("name"));
            news.put("publishedAt", item.getString("publishedAt"));
            newsList.add(news);
        }

        return newsList;
    }



    public List<Map<String, String>> getCurrencyNews() {
        RestTemplate restTemplate = new RestTemplate();
        String query = "currency value increased and decreased";
        String url = BASE_URL + "?q=" + query + "&language=en&sortBy=publishedAt&pageSize=15&apiKey=" + API_KEY;

        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = new JSONObject(response);

        List<Map<String, String>> newsList = new ArrayList<>();
        JSONArray articles = json.getJSONArray("articles");

        for (int i = 0; i < articles.length(); i++) {
            JSONObject item = articles.getJSONObject(i);
            Map<String, String> news = new HashMap<>();
            news.put("headline", item.getString("title"));
            news.put("url", item.getString("url"));
            news.put("source", item.getJSONObject("source").getString("name"));
            news.put("publishedAt", item.getString("publishedAt"));
            newsList.add(news);
        }

        return newsList;
    }


}
