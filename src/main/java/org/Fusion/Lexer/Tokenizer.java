package org.Fusion.Lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {

    private final List<TokenRule> tokenRules;

    public Tokenizer() {
        // Initialize the list of token rules
        tokenRules = new ArrayList<>();

        // Add rules for each token type
        tokenRules.add(new TokenRule("bot\\s+\"([^\"]+)\"", matcher -> new Token(TokenType.BOT, matcher.group(1))));
        tokenRules.add(new TokenRule("token\\s+\"([^\"]+)\"", matcher -> new Token(TokenType.TOKEN, matcher.group(1))));
        tokenRules.add(new TokenRule("on_ready\\s+\\{", matcher -> new Token(TokenType.ON_READY, "on_ready")));
        tokenRules.add(new TokenRule("command\\s+\"([^\"]+)\"\\s+\\{", matcher -> new Token(TokenType.COMMAND, matcher.group(1))));
        tokenRules.add(new TokenRule("reply\\s+\"([^\"]+)\"", matcher -> new Token(TokenType.REPLY, matcher.group(1))));
        tokenRules.add(new TokenRule("log\\s+\"([^\"]+)\"", matcher -> new Token(TokenType.LOG, matcher.group(1))));
    }

    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        String remainingInput = input;

        while (!remainingInput.isEmpty()) {
            boolean matched = false;

            for (TokenRule rule : tokenRules) {
                Matcher matcher = rule.getPattern().matcher(remainingInput);
                if (matcher.find()) {
                    tokens.add(rule.createToken(matcher));
                    remainingInput = remainingInput.substring(matcher.end());
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                // If no pattern matched, create an unknown token for the remaining input
                tokens.add(new Token(TokenType.UNKNOWN, remainingInput));
                break;
            }
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    // Method to add new token rules dynamically (optional)
    public void addTokenRule(TokenRule rule) {
        tokenRules.add(rule);
    }
}
