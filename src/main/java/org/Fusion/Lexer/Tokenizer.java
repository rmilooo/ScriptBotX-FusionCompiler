package org.Fusion.Lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

public class Tokenizer {

    // Map to store TokenType and its corresponding regex pattern
    private static final Map<TokenType, String> TOKEN_PATTERNS = new HashMap<>();

    static {
        // Add regex patterns for each TokenType
        TOKEN_PATTERNS.put(TokenType.BOT, "bot");
        TOKEN_PATTERNS.put(TokenType.TOKEN, "token");
        TOKEN_PATTERNS.put(TokenType.ON_READY, "on_ready");
        TOKEN_PATTERNS.put(TokenType.COMMAND, "command");
        TOKEN_PATTERNS.put(TokenType.REPLY, "reply");
        TOKEN_PATTERNS.put(TokenType.USER, "\\{user\\}");
        TOKEN_PATTERNS.put(TokenType.STRING, "\"[^\"]*\"");
        TOKEN_PATTERNS.put(TokenType.LOG, "log");
        TOKEN_PATTERNS.put(TokenType.SET_COMMAND_PREFIX, "commandPrefix");
        TOKEN_PATTERNS.put(TokenType.WHITESPACE, "\\s+");
        TOKEN_PATTERNS.put(TokenType.BRACE, "[\\{\\}]");
        TOKEN_PATTERNS.put(TokenType.UNKNOWN, ".");
        TOKEN_PATTERNS.put(TokenType.EOF, "$");
    }

    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder combinedPattern = new StringBuilder();

        // Build combined regex pattern by concatenating all individual patterns
        for (Map.Entry<TokenType, String> entry : TOKEN_PATTERNS.entrySet()) {
            combinedPattern.append("(").append(entry.getValue()).append(")|");
        }

        // Remove the last "|" to form a valid regex
        combinedPattern.deleteCharAt(combinedPattern.length() - 1);

        // Create a Pattern object
        Pattern pattern = Pattern.compile(combinedPattern.toString());
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            // Match the group that was found
            for (Map.Entry<TokenType, String> entry : TOKEN_PATTERNS.entrySet()) {
                String pattern2 = entry.getValue();
                String matched = matcher.group();
                if (matched != null && matched.matches(pattern2)) {
                    if (matched.trim().isEmpty()) continue;  // Skip empty strings (whitespace)

                    Token token = new Token(entry.getKey(), matched);
                    tokens.add(token);
                    break;
                }
            }
        }

        return tokens;
    }
}
