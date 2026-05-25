package com.enonic.xp.perftest.content;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public final class BenchmarkMain
{
    public static void main( final String[] args )
        throws Exception
    {
        final Options opts = new OptionsBuilder()
            .include( "com\\.enonic\\.xp\\.perftest\\.content\\..*Benchmark" )
            .resultFormat( ResultFormatType.JSON )
            .result( "results.json" )
            .output( "results.txt" )
            .build();
        new Runner( opts ).run();

        ReportWriter.write( "results.json", "results.md" );
    }
}
