package com.smartstock;
 // ← adjust to your package structure

import com.smartstock.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/currency")
@CrossOrigin(origins = "*")
public class NewsController {

    @Autowired
    private NewsService newsService;

    /**
     * GET /api/currency/news
     */
    @GetMapping("/news")
    public ResponseEntity<List<Map<String, String>>> getCurrencyNews() {
        List<Map<String, String>> articles = newsService.getCurrencyNews();
        return ResponseEntity.ok(articles);
    }
}
