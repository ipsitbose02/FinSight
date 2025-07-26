package com.smartstock;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.smartstock.ConversionDto;
import com.smartstock.CurrencyCodeDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/currency")
public class CurrencyController {

    private final String apiKey = "7338c543eb1242ed8770f1d6";
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/symbols")
    public ResponseEntity<List<CurrencyCodeDto>> getSymbols() {
        String url = String.format(
                "https://v6.exchangerate-api.com/v6/%s/codes",
                apiKey
        );
        JsonNode root = restTemplate.getForObject(url, JsonNode.class);
        List<CurrencyCodeDto> list = new ArrayList<>();
        if (root != null && root.path("supported_codes").isArray()) {
            for (JsonNode pair : root.get("supported_codes")) {
                String code = pair.get(0).asText();
                String name = pair.get(1).asText();
                list.add(new CurrencyCodeDto(code, name));
            }
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/convert")
    public ResponseEntity<ConversionDto> convert(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount
    ) {
        String amt = amount.toPlainString();
        String url = String.format(
                "https://v6.exchangerate-api.com/v6/%s/pair/%s/%s/%s",
                apiKey, from, to, amt
        );
        try {
            JsonNode root = restTemplate.getForObject(url, JsonNode.class);
            double rate = root.path("conversion_rate").asDouble();
            return ResponseEntity.ok(new ConversionDto(
                    rate,
                    rate * amount.doubleValue()
            ));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(502)
                    .body(new ConversionDto(0, 0));
        }
    }
}
