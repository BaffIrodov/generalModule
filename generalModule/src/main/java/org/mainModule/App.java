package org.mainModule;

import org.mainModule.parsers.ResultsPageParser;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{

    private static final ResultsPageParser mainPageResults = new ResultsPageParser();

    public static void main( String[] args ) throws IOException {
        System.out.println( "Hello World!" );
        mainPageResults.parseResults(1);
    }
}
