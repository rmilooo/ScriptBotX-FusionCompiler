package org.Fusion.Compiler;

import org.Fusion.Lexer.Token;
import org.Fusion.Lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Compiler {
    private final List<List<Object>> tokens;
    private String botToken = null;
    public Compiler(List<List<Object>> tokens) {
        this.tokens = tokens;
    }

    /**
     * Converts a single list into a list of tokens and adds it to the main token list.
     */
    public void convertToTokens(List<Object> inputList) {
        List<Object> tokenList = new ArrayList<>();
        for (Object item : inputList) {
            if (item instanceof Token token) {
                tokenList.add(token);
            }
        }
        tokens.add(tokenList);
    }

    /**
     * Compiles the tokens into a Python script as a String.
     */
    public String compileToPython() {
        StringBuilder pythonCode = new StringBuilder();
        pythonCode.append("import discord\n");
        pythonCode.append("import tasks\n");
        pythonCode.append("from discord.ext import commands\n\n");

        pythonCode.append("intents = discord.Intents.default()\n" +
                "intents.messages = True  # Enable message events\n" +
                "intents.message_content = True  # Enable message content access (required for message commands)");



        for (List<Object> tokenList : tokens) {
            processTokenList(tokenList, pythonCode, 0);
        }
        pythonCode.append("\nbot.run(TOKEN)\n");


        return pythonCode.toString();
    }

    /**
     * Processes a token list and appends the corresponding Python code, handling nested structures.
     */
    private void processTokenList(List<Object> tokenList, StringBuilder pythonCode, int indentLevel) {
        for (Object item : tokenList) {
            if (item instanceof Token token) {
                pythonCode.append("    ".repeat(indentLevel)).append(tokenToPython(token)).append("\n");
            } else if (item instanceof List<?> innerList) {
                processTokenList((List<Object>) innerList, pythonCode, indentLevel + 1);
            } else if (item instanceof TokenType tokenType && tokenType == TokenType.BRACE) {
                pythonCode.append("    ".repeat(indentLevel)).append("}\n");
            }
        }
    }

    /**
     * Converts a single token into a Python code snippet.
     */
    private String tokenToPython(Token token) {
        switch (token.type()) {
            case BOT -> {
                return "botname = " + token.value();
            }
            case TOKEN -> {
                setBotToken(token.value());
                return "TOKEN = " + token.value();
            }
            case SET_COMMAND_PREFIX -> {
                return "command_prefix = " + token.value() + "\n" +
                        "bot = commands.Bot(command_prefix=command_prefix, intents=intents)";
            }
            case LOG -> {
                return "print(" + token.value() + ")";
            }
            case COMMAND -> {
                return "@bot.command()\nasync def " + token.value().replace("\"","") + "(ctx):";
            }
            case REPLY -> {
                return "    await ctx.send(" + token.value() + ")";
            }
            case ON_READY -> {
                return "@bot.event\nasync def on_ready():";
            }
            case VAR -> {
                return token.value();
            }
            default -> {
                return "# Unhandled token type: " + token.type();
            }
        }
    }
    public void setBotToken(String token) {
        this.botToken = token;
    }
    public String getBotToken() {
        return botToken;
    }
}
