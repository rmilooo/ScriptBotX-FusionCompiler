package org.Fusion.Test;

import org.Fusion.Lexer.Token;
import org.Fusion.Lexer.TokenRule;
import org.Fusion.Lexer.TokenType;
import org.Fusion.Lexer.Tokenizer;

import java.util.List;

public class Compiler {
    public static void main(String[] args) {
        String botScript = """
            bot "MyFirstBot" {
                token "YOUR_BOT_TOKEN_HERE"
                
                on_ready {
                    log "Bot is online and ready!"
                }
                
                command "!ping" {
                    reply "Pong!"
                }
                
                command "!hello" {
                    reply "Hello, {user}!"
                }
            }
        """;

        // Create a new tokenizer
        Tokenizer tokenizer = new Tokenizer();

        // Optionally, add a new token rule at runtime (for example, a new "variable" token)
        tokenizer.addTokenRule(new TokenRule("variable\\s+\"([^\"]+)\"", matcher -> new Token(TokenType.UNKNOWN, matcher.group(1))));

        // Tokenize the script
        List<Token> tokens = tokenizer.tokenize(botScript);

        // Print the tokens
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
