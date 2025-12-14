package co.za.Main.WebTradeApplication;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.math.BigDecimal;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import co.za.Main.TradeModules.TradeAction;

public class WebServerApplication {
    
    private HttpServer server;
    private String filename = "trade-index.html";
    
    public WebServerApplication() throws IOException {
        setupServer();
    }
    
    private void setupServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/data", new DataHandler());
        server.createContext("/api/update", new UpdateHandler());
        server.createContext("/api/query", new QueryHandler());
        server.createContext("/api/reset", new ResetHandler());
        
        server.start();
        System.out.println("Trade Web Server running at: http://localhost:8080");
    }
    
    class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String html = getHtmlContent();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, html.getBytes().length);
                exchange.getResponseBody().write(html.getBytes());
                exchange.getResponseBody().close();
            }
        }
    }
    
    class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().close();
                return;
            }
            
            if ("GET".equals(exchange.getRequestMethod())) {
                try (WebAppDataBase db = new WebAppDataBase()) {
                    String json = buildDataJson(db);
                    sendJsonResponse(exchange, json);
                } catch (Exception e) {
                    sendErrorResponse(exchange, "Error fetching data: " + e.getMessage());
                }
            }
        }
    }
    
    class UpdateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().close();
                return;
            }
            
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String body = readBody(exchange);
                    
                    String variable = parseStringValue(body, "variable");
                    String column = parseStringValue(body, "column");
                    BigDecimal value = new BigDecimal(parseStringValue(body, "value"));
                    
                    try (WebAppDataBase db = new WebAppDataBase()) {
                        db.updateValue(variable, column, value);
                        sendJsonResponse(exchange, "{\"success\":true,\"message\":\"Value updated successfully\"}");
                    }
                    
                } catch (Exception e) {
                    sendErrorResponse(exchange, "Error updating value: " + e.getMessage());
                }
            }
        }
    }
    
    class ResetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().close();
                return;
            }
            
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String body = readBody(exchange);
                    String resetType = parseStringValue(body, "resetType");
                    
                    try (WebAppDataBase db = new WebAppDataBase()) {
                        switch (resetType) {
                            case "zero":
                                db.resetAllValuesToZero();
                                sendJsonResponse(exchange, "{\"success\":true,\"message\":\"All values reset to zero\"}");
                                break;
                            case "input":
                                db.resetInputValuesToZero();
                                sendJsonResponse(exchange, "{\"success\":true,\"message\":\"Input values reset to zero\"}");
                                break;
                            default:
                                sendErrorResponse(exchange, "Invalid reset type: " + resetType);
                        }
                    }
                    
                } catch (Exception e) {
                    sendErrorResponse(exchange, "Error resetting values: " + e.getMessage());
                }
            }
        }
    }
    
    class QueryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().close();
                return;
            }
            
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String body = readBody(exchange);
                    
                    // Parse trade action (SELL or BUY)
                    String tradeActionStr = parseStringValue(body, "tradeAction");
                    TradeAction tradeAction = "BUY".equalsIgnoreCase(tradeActionStr) 
                        ? TradeAction.BUY 
                        : TradeAction.SELL;
                    
                    boolean basedOnMarketRate = parseBooleanValue(body, "basedOnMarketRate");
                    BigDecimal spread = parseDecimalValue(body, "spread", new BigDecimal("0.001"));
                    BigDecimal ratePK = parseDecimalValue(body, "ratePK", new BigDecimal("0.95"));
                    BigDecimal ratePN = parseDecimalValue(body, "ratePN", new BigDecimal("0.98"));
                    
                    System.out.println("Query Parameters:");
                    System.out.println("- Trade Action: " + tradeAction);
                    System.out.println("- Based on Market Rate: " + basedOnMarketRate);
                    System.out.println("- Spread: " + spread);
                    System.out.println("- Rate PK: " + ratePK);
                    System.out.println("- Rate PN: " + ratePN);
                    
                    try (WebAppDataBase db = new WebAppDataBase()) {
                        WebQueryImplementation queryImpl = new WebQueryImplementation(
                            tradeAction, basedOnMarketRate, spread, ratePK, ratePN);
                        queryImpl.populateTable(db);
                        
                        String json = buildDataJson(db);
                        String responseMsg = String.format(
                            "Query executed successfully! (Action: %s, Mode: %s, Spread: %s, RatePK: %s, RatePN: %s)", 
                            tradeAction,
                            basedOnMarketRate ? "Market-Based" : "Execution-Based", 
                            spread, ratePK, ratePN
                        );
                        
                        sendJsonResponse(exchange, "{\"success\":true,\"message\":\"" + responseMsg + "\",\"data\":" + json + "}");
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    sendErrorResponse(exchange, "Error running query: " + e.getMessage());
                }
            }
        }
    }
    
    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Content-Type", "application/json");
    }
    
    private String readBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            return reader.lines().reduce("", (a, b) -> a + b);
        }
    }
    
    private String parseStringValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : "";
    }
    
    private boolean parseBooleanValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(true|false)");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() && "true".equals(matcher.group(1));
    }
    
    private BigDecimal parseDecimalValue(String json, String key, BigDecimal defaultValue) {
        try {
            Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*([0-9]*\\.?[0-9]+)");
            Matcher matcher = pattern.matcher(json);
            return matcher.find() ? new BigDecimal(matcher.group(1)) : defaultValue;
        } catch (NumberFormatException e) {
            System.err.println("Error parsing decimal value for " + key + ", using default: " + defaultValue);
            return defaultValue;
        }
    }
    
    private void sendJsonResponse(HttpExchange exchange, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, json.getBytes().length);
        exchange.getResponseBody().write(json.getBytes());
        exchange.getResponseBody().close();
    }
    
    private void sendErrorResponse(HttpExchange exchange, String error) throws IOException {
        String json = "{\"success\":false,\"error\":\"" + error.replace("\"", "\\\"") + "\"}";
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(500, json.getBytes().length);
        exchange.getResponseBody().write(json.getBytes());
        exchange.getResponseBody().close();
    }
    
    private String buildDataJson(WebAppDataBase db) throws Exception {
        String[] variables = {"tradeprofit", "profitfactor", "tradeamount", "openingvalue", "closingvalue"};
        StringBuilder json = new StringBuilder("{\"data\":[");
        
        for (int i = 0; i < variables.length; i++) {
            String var = variables[i];
            BigDecimal max = db.getValueFromColumn(var, "maximum");
            BigDecimal min = db.getValueFromColumn(var, "minimum");
            BigDecimal returnMin = db.getValueFromColumn(var, "returnmin");
            BigDecimal returnMax = db.getValueFromColumn(var, "returnmax");
            
            json.append(String.format(
                "{\"variable\":\"%s\",\"maximum\":\"%s\",\"minimum\":\"%s\"," +
                "\"returnmin\":\"%s\",\"returnmax\":\"%s\"}",
                var, max.toPlainString(), min.toPlainString(),
                returnMin.toPlainString(), returnMax.toPlainString()));
            
            if (i < variables.length - 1) json.append(",");
        }
        
        json.append("]}");
        return json.toString();
    }
    
    private String getHtmlContent() {
        try {
            return new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get(filename)));
        } catch (Exception e) {
            System.err.println("Could not find " + filename + ", using fallback HTML");
            return getFallbackHtml();
        }
    }
    
    private String getFallbackHtml() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Trade Application</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    .error { color: red; background: #ffe6e6; padding: 20px; border-radius: 5px; }
                </style>
            </head>
            <body>
                <h1>Trade Web Application</h1>
                <div class="error">
                    <h3>Missing Interface File</h3>
                    <p>Please save the updated HTML interface as <strong>%s</strong> in your project root directory.</p>
                    <p>The interface includes controls for trade action (SELL/BUY), spread, ratePK, ratePN, and basedOnMarketRate toggle.</p>
                </div>
            </body>
            </html>
            """.formatted(filename);
    }

    public void stop() {
        if (server != null) server.stop(0);
    }
}