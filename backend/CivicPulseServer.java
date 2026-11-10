
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class CivicPulseServer {

    static final List<Map<String, String>> complaints =
        new ArrayList<>();

    static final Path FILE = Paths.get("complaints.txt");

    static long nextId = 1;

    static String encode(String value) {
        return Base64.getEncoder().encodeToString(
            value.getBytes(StandardCharsets.UTF_8)
        );
    }

    static String decode(String value) {
        return new String(
            Base64.getDecoder().decode(value),
            StandardCharsets.UTF_8
        );
    }

    static void loadData() {
        try {
            if (!Files.exists(FILE)) return;

            for (String line : Files.readAllLines(FILE)) {
                String[] p = line.split("\\|", -1);

                if (p.length != 7) continue;

                Map<String, String> c = new LinkedHashMap<>();

                c.put("id", p[0]);
                c.put("name", decode(p[1]));
                c.put("category", decode(p[2]));
                c.put("title", decode(p[3]));
                c.put("description", decode(p[4]));
                c.put("date", decode(p[5]));
                c.put("status", decode(p[6]));

                complaints.add(c);

                nextId = Math.max(
                    nextId,
                    Long.parseLong(p[0]) + 1
                );
            }

        } catch (Exception e) {
            System.out.println(
                "Data loading error: " + e.getMessage()
            );
        }
    }

    static void saveData() {
        try {
            List<String> lines = new ArrayList<>();

            for (Map<String, String> c : complaints) {
                lines.add(
                    c.get("id") + "|" +
                    encode(c.get("name")) + "|" +
                    encode(c.get("category")) + "|" +
                    encode(c.get("title")) + "|" +
                    encode(c.get("description")) + "|" +
                    encode(c.get("date")) + "|" +
                    encode(c.get("status"))
                );
            }

            Files.write(FILE, lines);

        } catch (Exception e) {
            System.out.println(
                "Data saving error: " + e.getMessage()
            );
        }
    }

    static Map<String, String> parseForm(String body) {
        Map<String, String> data = new HashMap<>();

        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);

            try {
                String key = URLDecoder.decode(
                    parts[0], "UTF-8"
                );

                String value = parts.length > 1
                    ? URLDecoder.decode(parts[1], "UTF-8")
                    : "";

                data.put(key, value);

            } catch (Exception ignored) {}
        }

        return data;
    }

    static String escape(String value) {
        if (value == null) return "";

        return value.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    static String toJson(Map<String, String> c) {
        return "{"
            + "\"id\":" + c.get("id") + ","
            + "\"name\":\"" + escape(c.get("name")) + "\","
            + "\"category\":\"" + escape(c.get("category")) + "\","
            + "\"title\":\"" + escape(c.get("title")) + "\","
            + "\"description\":\"" + escape(c.get("description")) + "\","
            + "\"date\":\"" + escape(c.get("date")) + "\","
            + "\"status\":\"" + escape(c.get("status")) + "\""
            + "}";
    }

    static void send(
        HttpExchange exchange,
        int code,
        String response
    ) throws IOException {

        exchange.getResponseHeaders().set(
            "Content-Type",
            "application/json; charset=UTF-8"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin", "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods",
            "GET, POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers",
            "Content-Type"
        );

        byte[] bytes = response.getBytes(
            StandardCharsets.UTF_8
        );

        exchange.sendResponseHeaders(code, bytes.length);

        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    public static void main(String[] args) throws Exception {

        loadData();

        int port = Integer.parseInt(
            System.getenv().getOrDefault("PORT", "8000")
        );

        HttpServer server = HttpServer.create(
            new InetSocketAddress("0.0.0.0", port),
            0
        );

        server.createContext("/api/complaints", exchange -> {

            try {
                String method = exchange.getRequestMethod();

                String path = exchange.getRequestURI().getPath();

                if (method.equals("OPTIONS")) {
                    send(exchange, 200, "{\"message\":\"OK\"}");
                    return;
                }

                synchronized (complaints) {

                    // GET ALL COMPLAINTS
                    if (method.equals("GET") &&
                        path.equals("/api/complaints")) {

                        StringBuilder json =
                            new StringBuilder("[");

                        for (int i = 0; i < complaints.size(); i++) {

                            if (i > 0) json.append(",");

                            json.append(
                                toJson(complaints.get(i))
                            );
                        }

                        json.append("]");

                        send(exchange, 200, json.toString());
                    }

                    // SUBMIT NEW COMPLAINT
                    else if (method.equals("POST") &&
                        path.equals("/api/complaints")) {

                        String body = new String(
                            exchange.getRequestBody().readAllBytes(),
                            StandardCharsets.UTF_8
                        );

                        Map<String, String> data =
                            parseForm(body);

                        Map<String, String> c =
                            new LinkedHashMap<>();

                        c.put("id", String.valueOf(nextId++));

                        c.put("name",
                            data.getOrDefault("name", ""));

                        c.put("category",
                            data.getOrDefault("category", ""));

                        c.put("title",
                            data.getOrDefault("title", ""));

                        c.put("description",
                            data.getOrDefault("description", ""));

                        c.put("date",
                            java.time.LocalDate.now().toString());

                        c.put("status", "Pending");

                        complaints.add(c);

                        saveData();

                        send(exchange, 201, toJson(c));
                    }

                    // UPDATE COMPLAINT STATUS
                    else if (method.equals("POST") &&
                        path.equals("/api/complaints/status")) {

                        String body = new String(
                            exchange.getRequestBody().readAllBytes(),
                            StandardCharsets.UTF_8
                        );

                        Map<String, String> data =
                            parseForm(body);

                        String id = data.get("id");

                        String status = data.get("status");

                        boolean updated = false;

                        for (Map<String, String> c : complaints) {

                            if (c.get("id").equals(id)) {

                                c.put("status", status);

                                updated = true;

                                break;
                            }
                        }

                        if (updated) {

                            saveData();

                            send(exchange, 200,
                                "{\"message\":\"Status updated successfully\"}");

                        } else {

                            send(exchange, 404,
                                "{\"error\":\"Complaint not found\"}");
                        }
                    }

                    else {
                        send(exchange, 404,
                            "{\"error\":\"Endpoint not found\"}");
                    }
                }

            } catch (Exception e) {

                e.printStackTrace();

                send(exchange, 500,
                    "{\"error\":\"Internal server error\"}");
            }
        });

        server.start();

        System.out.println("================================");
        System.out.println("     CivicPulse Java Backend");
        System.out.println("================================");
        System.out.println(
            "Server running on port: " + port
        );
        System.out.println(
            "API: /api/complaints"
        );
        System.out.println("Press Ctrl+C to stop.");
    }
}
