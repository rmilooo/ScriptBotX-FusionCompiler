package org.Fusion.Compiler;

import org.Fusion.Lexer.Token;

import java.util.ArrayList;
import java.util.List;

public class Compiler {
    private List<List<Object>> tokens;

    public Compiler(List<List<Object>> tokens) {
        this.tokens = tokens;
    }

    // Function to convert a single list into individual tokens and put them into token lists
    public void convertToTokens(List<Object> inputList) {
        List<Object> tokenList = new ArrayList<>();

        for (Object item : inputList) {
            if (item instanceof Token token) {
                // Add the token to the new token list
                tokenList.add(token);
            }
        }

        // After converting all items, add the tokenList to the tokens
        tokens.add(tokenList);
    }

    public String compileToPython() {
        StringBuilder pythonCode = new StringBuilder();
        pythonCode.append("import discord\n");
        pythonCode.append("from discord.ext import commands\n\n");

        int index = 0;
        while (index < tokens.size()) {  // Process each token list
            List<Object> tokenList = tokens.get(index);
            int index2 = 0;
            while (index2 < tokenList.size()) {
                // Check if the element is an instance of List and then cast it properly
                if (tokenList.get(index2) instanceof List) {
                    List<Object> innerList = (List<Object>) tokenList.get(index2); // Cast to List<Object>
                    for (Object innerToken : innerList) {
                        if (innerToken instanceof Token token) {
                            pythonCode.append(tokenToPython(token)).append("\n");
                        }
                    }
                }else if (tokenList.get(index2) instanceof Token token) {
                    pythonCode.append(tokenToPython(token)).append("\n");
                }
                index2++;
            }
            index++;
        }

        return pythonCode.toString();
    }
    private String tokenToPython(Token token) {
            switch (token.type()) {
                case BOT -> {
                    return "botname = "+token.value();
                }
                default -> System.out.println("Unhandled token type: " + token);
            }

        return "";
    }
}
