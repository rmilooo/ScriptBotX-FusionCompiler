package org.Fusion.Lexer;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenRule {
    private final Pattern pattern;
    private final Function<Matcher, Token> tokenFactory;

    public TokenRule(String regex, Function<Matcher, Token> tokenFactory) {
        this.pattern = Pattern.compile(regex);
        this.tokenFactory = tokenFactory;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Token createToken(Matcher matcher) {
        return tokenFactory.apply(matcher);
    }
}
