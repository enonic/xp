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
        // an argument narrows the run to the benchmarks whose name matches it, for measuring one operation at a time
        final String include =
            args.length > 0 ? args[0] : "com\\.enonic\\.xp\\.perftest\\.content\\..*Benchmark";

        final Options opts = new OptionsBuilder()
            .include( include )
            .resultFormat( ResultFormatType.JSON )
            .result( "results.json" )
            .output( "results.txt" )
            .build();
        new Runner( opts ).run();

        ReportWriter.write( "results.json", "results.md" );
    }
}
