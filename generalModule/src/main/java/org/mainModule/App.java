package org.mainModule;

import org.mainModule.parsers.MatchesPageParser;
import org.mainModule.parsers.ResultsPageParser;

import java.io.IOException;
public class App 
{

    private static final ResultsPageParser mainPageResults = new ResultsPageParser();
    private static final MatchesPageParser matchesPageParser = new MatchesPageParser();

    public static void main( String[] args ) throws IOException {
        matchesPageParser.parseMatches();
        mainPageResults.parseResults(1);
    }
}