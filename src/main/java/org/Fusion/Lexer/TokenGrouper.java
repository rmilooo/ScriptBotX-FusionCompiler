package org.Fusion.Lexer;

import org.Fusion.Main.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

public class TokenGrouper {
    private final List<Token> tokens;

    public TokenGrouper(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<List<Object>> group() {
        List<List<Object>> groupedTokens = new ArrayList<>();
        int index = 0;

        // Start grouping tokens
        while (index < tokens.size()) {
            Token currentToken = tokens.get(index);

            // Handle the 'bot' declaration
            if (currentToken.getType() == TokenType.BOT) {
                List<Object> botGroup = groupBot(index);
                if (botGroup != null) {
                    groupedTokens.add(botGroup);
                    index += 3;
                } else {
                    ErrorHandler.handleError("Invalid bot declaration.");
                    break;
                }
            }
            // Handle 'command' declaration
            else if (currentToken.getType() == TokenType.COMMAND) {
                GroupingResult result = groupCommand(index);
                List<Object> commandGroup = result.getGroupedTokens();
                if (commandGroup != null) {
                    groupedTokens.add(commandGroup);
                    index += commandGroup.size();
                } else {
                    ErrorHandler.handleError("Invalid command declaration.");
                    break;
                }
            }
            // Handle 'on_ready' declaration
            else if (currentToken.getType() == TokenType.ON_READY) {
                GroupingResult result = groupOnReady(index);
                assert result != null;
                List<Object> onReadyGroup = result.getGroupedTokens();
                if (onReadyGroup != null) {
                    groupedTokens.add(onReadyGroup);
                    index = result.getNewIndex();
                } else {
                    ErrorHandler.handleError("Invalid 'on_ready' declaration.");
                    break;
                }
            }

            // Handle 'token' declaration
            else if (currentToken.getType() == TokenType.TOKEN) {
                if (index + 1 < tokens.size() && tokens.get(index + 1).getType() == TokenType.STRING) {
                    // Add the 'token' declaration and its associated value
                    groupedTokens.add(List.of(new Token(TokenType.TOKEN, tokens.get(index + 1).getValue())));
                    // Skip past 'token' and the token value
                    index += 2; // Move index ahead by 2 (one for 'token', one for the associated STRING value)
                } else {
                    // Handle error or invalid syntax if next token isn't a STRING
                    ErrorHandler.handleError("Invalid token declaration. Expected a STRING value after 'token' keyword.");
                }
            }
            // Handle 'log' declaration
            else if (currentToken.getType() == TokenType.LOG) {
                List<Object> logGroup = groupLog(index);
                if (logGroup!= null) {
                    groupedTokens.add(logGroup);
                    index += logGroup.size() + 1;
                } else {
                    ErrorHandler.handleError("Invalid 'log' declaration.");
                    break;
                }
            }
            // Handle 'commandPrefix' declaration
            else if (currentToken.getType() == TokenType.SET_COMMAND_PREFIX){
                if (index + 1 < tokens.size() && tokens.get(index + 1).getType() == TokenType.STRING) {
                    // Add the 'token' declaration and its associated value
                    groupedTokens.add(List.of(new Token(TokenType.SET_COMMAND_PREFIX, tokens.get(index + 1).getValue())));
                    // Skip past 'token' and the token value
                    index += 2; // Move index ahead by 2 (one for 'token', one for the associated STRING value)
                } else {
                    // Handle error or invalid syntax if next token isn't a STRING
                    ErrorHandler.handleError("Invalid token declaration. Expected a STRING value after 'token' keyword.");
                }
            }
            // Handle unexpected tokens
            else {
                if (currentToken.getType() == TokenType.BRACE){
                    if (tokens.getLast() == currentToken){
                        index += 1;
                        break;
                    }
                }
                ErrorHandler.handleError("Unexpected token: " + currentToken.getValue() + " Index: "+index);
                break;
            }
        }

        return groupedTokens;
    }

    // Grouping logic for 'bot' declarations
    private List<Object> groupBot(int index) {
        List<Object> botGroup = new ArrayList<>();

        // Check if the next token is a string (i.e., the bot name)
        if (index + 1 < tokens.size() && tokens.get(index + 1).getType() == TokenType.STRING) {
            botGroup.add(new Token(TokenType.BOT, tokens.get(index + 1).getValue()));
            index += 2; // Skip past 'bot' and the bot name (assuming bot declaration is like 'bot "MyBot"')
        } else {
            // Error handling if bot name is not found after 'bot'
            ErrorHandler.handleError("Invalid bot declaration. Expected a BotName after 'bot' keyword.");
            return null;
        }
        return botGroup;
    }

    // Grouping logic for 'command' declarations
    private GroupingResult groupCommand(int index) {
        List<Object> commandGroup = new ArrayList<>();

        if (index + 1 < tokens.size() && tokens.get(index + 1).getType() == TokenType.STRING) {
            commandGroup.add(new Token(TokenType.COMMAND, tokens.get(index + 1).getValue()));
            index += 2; // Skip over the command name

            // Now look for 'reply' tokens
            if (index + 1 < tokens.size() && tokens.get(index + 1).getType() == TokenType.REPLY) {
                List<Object> replyGroup = groupReply(index);
                commandGroup.add(replyGroup);
                index += replyGroup.size();
            } else {
                ErrorHandler.handleError("Command missing 'reply' declaration.");
                return null;
            }
        } else {
            ErrorHandler.handleError("Invalid command declaration. Expected a command name after 'command' keyword.");
            return null;
        }

        return new GroupingResult(commandGroup, index);
    }

    // Grouping logic for 'reply' declarations
    private List<Object> groupReply(int index) {
        List<Object> replyGroup = new ArrayList<>();

        if (index + 1 < tokens.size() && tokens.get(index + 1).getType() == TokenType.STRING) {
            replyGroup.add(new Token(TokenType.REPLY, tokens.get(index + 1).getValue()));
            index++; // Skip past the 'reply' value
        } else {
            ErrorHandler.handleError("Expected a string value after 'reply' in command.");
            return null;
        }

        return replyGroup;
    }

    // Grouping logic for 'on_ready' declarations
    private GroupingResult groupOnReady(int index) {
        List<Object> onReadyGroup = new ArrayList<>();

        // Expect '{' after 'on_ready'
        if (index + 1 < tokens.size() && tokens.get(index + 1).getType() == TokenType.BRACE) {
            onReadyGroup.add(new Token(TokenType.ON_READY, tokens.get(index + 1).getValue()));
            index++; // Move past the opening brace

            // Create a new TokenGrouper for the content inside the braces
            List<Token> onReadyTokens = new ArrayList<>();
            index++; // Skip the opening brace
            while (index < tokens.size() && tokens.get(index).getType() != TokenType.BRACE) {
                onReadyTokens.add(tokens.get(index));
                index++;
            }

            // Now, group the tokens inside the on_ready block
            TokenGrouper innerGrouper = new TokenGrouper(onReadyTokens);
            List<List<Object>> groupedOnReadyContent = innerGrouper.group(); // Group the content inside 'on_ready'
            onReadyGroup.addAll(groupedOnReadyContent);

            // Check for closing brace '}'
            if (index < tokens.size() && tokens.get(index).getType() == TokenType.BRACE) {
                onReadyGroup.add(new Token(TokenType.BRACE, tokens.get(index).getValue()));
                index++; // Move past the closing brace
            } else {
                ErrorHandler.handleError("Expected closing brace '}' in on_ready block.");
                return null;
            }
        } else {
            ErrorHandler.handleError("Expected opening brace '{' in on_ready block.");
            return null;
        }

        return new GroupingResult(onReadyGroup, index);
    }

    // Grouping logic for 'log' declarations inside 'on_ready' or other blocks
    private List<Object> groupLog(int index) {
        List<Object> logGroup = new ArrayList<>();

        if (index + 1 < tokens.size() && tokens.get(index + 1).getType() == TokenType.STRING) {
            logGroup.add(new Token(TokenType.LOG, tokens.get(index + 1).getValue()));
            index++; // Skip past the 'log' value
        } else {
            ErrorHandler.handleError("Expected a string value after 'log'");
            return null;
        }

        return logGroup;
    }
}
