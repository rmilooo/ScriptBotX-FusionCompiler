package org.Fusion.Lexer;

import java.util.List;

public class GroupingResult {
    private List<Object> groupedTokens;
    private int newIndex;

    public GroupingResult(List<Object> groupedTokens, int newIndex) {
        this.groupedTokens = groupedTokens;
        this.newIndex = newIndex;
    }

    public List<Object> getGroupedTokens() {
        return groupedTokens;
    }

    public int getNewIndex() {
        return newIndex;
    }
}
