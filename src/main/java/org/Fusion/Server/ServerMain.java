package org.Fusion.Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.Fusion.Compiler.Compiler;
import org.Fusion.Lexer.Token;
import org.Fusion.Lexer.TokenGrouper;
import org.Fusion.Lexer.Tokenizer;
import org.Fusion.Test.StaticStuff;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ServerMain {
    public static void main(String[] args) {
        try {
            // Start the embedded HTTP server
            int port = 9576;
            HttpServer server = HttpServer.create(new java.net.InetSocketAddress(port), 0);
            server.createContext("/compile", exchange -> {
                System.out.println("Connect from: " + exchange.getRemoteAddress().toString());

                // Read the request body to get the JSON
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Received body: " + body);

                // Parse the JSON to extract the 'code' parameter
                String code = null;
                try {
                    JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                    code = jsonObject.get("code").getAsString();
                } catch (Exception e) {
                    System.out.println("Error parsing JSON: " + e.getMessage());
                    exchange.sendResponseHeaders(400, 0);
                    exchange.getResponseBody().write("Invalid JSON or missing 'code' parameter".getBytes());
                    exchange.close();
                    return;
                }

                if (code == null || code.isEmpty()) {
                    exchange.sendResponseHeaders(400, 0);
                    exchange.getResponseBody().write("Missing 'code' parameter".getBytes());
                    exchange.close();
                    return;
                }

                // Tokenize the code
                Tokenizer tokenizer = new Tokenizer();
                List<Token> tokens = tokenizer.tokenize(code);
                System.out.println(code);

                // Group tokens
                TokenGrouper grouper = new TokenGrouper(tokens);
                if (!grouper.isSuccess()) {
                    JsonObject errorResponse = new JsonObject();
                    errorResponse.addProperty("status", "error");
                    errorResponse.add("errors", new JsonArray());
                    grouper.getHandler().getAllErrors().forEach(error -> errorResponse.getAsJsonArray("errors").add(error));
                    String errorJson = errorResponse.toString();

                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(500, errorJson.length());
                    exchange.getResponseBody().write(errorJson.getBytes());
                    exchange.close();
                    return;
                }

                // Compile the code
                Compiler compiler = new Compiler(grouper.group());
                String pythonCode = compiler.compileToPython();

                // Construct the JSON response
                JsonObject response = new JsonObject();
                response.addProperty("status", "success");

                JsonObject compiledCode = new JsonObject();
                compiledCode.addProperty("language", "Fusion");
                compiledCode.addProperty("source", code);

                JsonArray tokenArray = new JsonArray();
                tokens.forEach(token -> {
                    JsonObject tokenJson = new JsonObject();
                    tokenJson.addProperty("type", token.type().toString()); // Replace with actual token type
                    tokenJson.addProperty("value", token.value()); // Replace with actual token value
                    tokenArray.add(tokenJson);
                });
                compiledCode.add("tokens", tokenArray);

                JsonObject output = new JsonObject();
                output.addProperty("language", "Python");
                output.addProperty("code", pythonCode);
                output.addProperty("token",compiler.getBotToken());
                compiledCode.add("output", output);

                response.add("compiled_code", compiledCode);

                System.out.println(pythonCode);

                String jsonResponse = response.toString();

                // Send the response
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonResponse.length());
                exchange.getResponseBody().write(jsonResponse.getBytes());
                exchange.close();

                // Cleanup
                grouper.handler.clearErrors();
            });

            server.start();
            System.out.println("Server started at http://localhost:" + port + "/compile");
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
