package com.smartstock;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "*") // Enable CORS for frontend
public class StockController {

    @Autowired
    private TwelveDataService twelveDataService;

    @GetMapping("/{symbol}")
    public ResponseEntity<?> getStockPrice(@PathVariable String symbol) {
        try {
            return ResponseEntity.ok(twelveDataService.getLatestStockPrice(symbol));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching stock data: " + e.getMessage());
        }
    }
    @GetMapping("/history/{symbol}")
    public ResponseEntity<?> getStockHistory(@PathVariable String symbol) {
        try {
            return ResponseEntity.ok(twelveDataService.getIntradayHistory(symbol));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching history: " + e.getMessage());
        }
    }
    @GetMapping("/history/{symbol}/{interval}/{outputSize}")
    public ResponseEntity<?> getStockHistoryFlexible(
            @PathVariable String symbol,
            @PathVariable String interval,
            @PathVariable int outputSize) {
        try {
            return ResponseEntity.ok(twelveDataService.getHistoricalData(symbol, interval, outputSize));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching history: " + e.getMessage());
        }
    }


    @Autowired
    private NewsService newsService;



    @GetMapping("/news/{symbol}")
    public ResponseEntity<?> getStockNews(@PathVariable String symbol) {
        try {
            return ResponseEntity.ok(newsService.getStockNews(symbol));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching news: " + e.getMessage());
        }
    }

    @GetMapping("/performance/{symbol}")
    public ResponseEntity<?> getPerformanceOverview(@PathVariable String symbol) {
        try {
            return ResponseEntity.ok(twelveDataService.getPerformanceReturns(symbol));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching performance data: " + e.getMessage());
        }
    }

    @GetMapping("/statistics/{symbol}")
    public ResponseEntity<?> getStockStatistics(@PathVariable String symbol) {
        try {
            return ResponseEntity.ok(twelveDataService.getStockStatistics(symbol));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching statistics: " + e.getMessage());
        }
    }

    @GetMapping("/compare")
    public ResponseEntity<?> compareStocks(@RequestParam List<String> symbols) {
        try {
            List<Map<String, String>> result = new ArrayList<>();
            for (String symbol : symbols) {
                Map<String, String> stats = twelveDataService.getStockStatistics(symbol);
                stats.put("symbol", symbol);
                result.add(stats);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error comparing stocks: " + e.getMessage());
        }
    }







}
