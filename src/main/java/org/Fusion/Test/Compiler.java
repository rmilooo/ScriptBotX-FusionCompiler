package org.Fusion.Test;

import org.Fusion.Lexer.Token;
import org.Fusion.Lexer.TokenGrouper;
import org.Fusion.Lexer.Tokenizer;

import java.util.List;

public class Compiler {
    public static void main(String[] args) {
        String botScript = """
            bot "MyFirstBot" {
                token "YOUR_BOT_TOKEN_HERE"
                commandPrefix "!"
                log "Bot is online and ready!"
                command "joke" {
                    reply "Why did the chicken join Discord? To cross the chat!"
                }
                on_ready {
                    log "Bot is online and ready!"
                    log "Bot is online and ready!"
                    log "Bot is online and ready!"
                    log "Bot is online and ready!"
                    log "Bot is online and ready!"
                    log "Bot is online and ready!"
                    log "Bot is online and ready!"
                    log "Bot is online and ready!"
                    var x = 2
                }
            }
        """;

        // Create a new tokenizer
        Tokenizer tokenizer = new Tokenizer();

        // Tokenize the script
        TokenGrouper grouper = new TokenGrouper(tokenizer.tokenize(botScript));

        // Group the tokens
        List<List<Object>> groupedTokens = grouper.group();

        // Print the grouped tokens (handling nested groups)
        printGroupedTokens(groupedTokens, 0);

        org.Fusion.Compiler.Compiler compiler = new org.Fusion.Compiler.Compiler(groupedTokens);

        System.out.println(compiler.compileToPython());

        //print(tokenizer.tokenize(botScript));

    }

    // Recursive method to print the grouped tokens, handling nested groups
    private static void printGroupedTokens(List<List<Object>> groupedTokens, int indentLevel) {
        String indent = "  ".repeat(indentLevel);

        for (Object group : groupedTokens) {
            if (group instanceof List<?>) {
                // If the group is a nested list, recurse and print its contents
                printGroupedTokens((List<List<Object>>) group, indentLevel + 1);
            } else if (group instanceof Token token) {
                // If it's a Token, print its details
                System.out.println(indent + "Type: " + token.type() + ", Value: " + token.value());
            }
        }
    }
    public static void print(List<Token> tokens){
        int index = 0;
        for (Token token : tokens){
            System.out.println(index+": "+token);
            index++;
        }
    }
}
