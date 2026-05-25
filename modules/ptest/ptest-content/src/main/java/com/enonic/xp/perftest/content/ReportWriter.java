package com.enonic.xp.perftest.content;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

final class ReportWriter
{
    static void write( final String jsonPath, final String mdPath )
        throws IOException
    {
        final List<Row> rows = parse( Files.readString( Paths.get( jsonPath ), StandardCharsets.UTF_8 ) );
        final SystemInfo info = SystemInfo.collect();

        final StringBuilder md = new StringBuilder( 1024 );
        md.append( "## ContentService benchmarks\n\n" );
        md.append( "Run: " ).append( Instant.now() ).append( "\n\n" );
        md.append( "| Benchmark | Mode | Score | Unit | Throughput |\n" );
        md.append( "|---|---|---:|---|---:|\n" );
        for ( final Row r : rows )
        {
            md.append( "| " ).append( r.benchmark ).append( " | " ).append( r.mode ).append( " | " );
            md.append( String.format( Locale.ROOT, "%.2f", r.score ) ).append( " | " );
            md.append( r.unit ).append( " | " );
            md.append( String.format( Locale.ROOT, "%.0f ops/s", r.opsPerSec() ) ).append( " |\n" );
        }
        md.append( "\n### Environment\n\n" );
        md.append( "- **CPU**: " ).append( info.cpuModel ).append( "\n" );
        md.append( "- **Logical cores**: " ).append( info.cores ).append( "\n" );
        md.append( "- **Memory**: " ).append( info.memoryGiB ).append( " GiB\n" );
        md.append( "- **OS**: " ).append( info.os ).append( "\n" );
        md.append( "- **JVM**: " ).append( info.jvm ).append( "\n" );

        Files.writeString( Paths.get( mdPath ), md.toString(), StandardCharsets.UTF_8 );
        System.out.println( "[ptest] markdown report: " + Paths.get( mdPath ).toAbsolutePath() );
    }

    private static List<Row> parse( final String json )
        throws IOException
    {
        final List<Row> rows = new ArrayList<>();
        final JsonNode arr = new ObjectMapper().readTree( json );
        for ( final JsonNode item : arr )
        {
            final String benchmark = shorten( item.path( "benchmark" ).asText() );
            final String mode = item.path( "mode" ).asText();
            final JsonNode metric = item.path( "primaryMetric" );
            final double score = metric.path( "score" ).asDouble();
            final String unit = metric.path( "scoreUnit" ).asText();
            rows.add( new Row( benchmark, mode, score, unit ) );
        }
        return rows;
    }

    private static String shorten( final String fqn )
    {
        // com.enonic.xp.perftest.content.ContentCreateBenchmark.create -> ContentCreateBenchmark.create
        final int dot = fqn.lastIndexOf( '.', fqn.lastIndexOf( '.' ) - 1 );
        return dot >= 0 ? fqn.substring( dot + 1 ) : fqn;
    }

    private static final class Row
    {
        final String benchmark;

        final String mode;

        final double score;

        final String unit;

        Row( final String benchmark, final String mode, final double score, final String unit )
        {
            this.benchmark = benchmark;
            this.mode = mode;
            this.score = score;
            this.unit = unit;
        }

        double opsPerSec()
        {
            // JMH avgt with us/op: ops/s = 1_000_000 / us-per-op
            // Other modes/units would need different math; treat as best-effort.
            switch ( unit )
            {
                case "us/op":
                    return 1_000_000.0 / score;
                case "ms/op":
                    return 1_000.0 / score;
                case "ns/op":
                    return 1_000_000_000.0 / score;
                case "s/op":
                    return 1.0 / score;
                default:
                    return Double.NaN;
            }
        }
    }

    private static final class SystemInfo
    {
        final String cpuModel;

        final int cores;

        final long memoryGiB;

        final String os;

        final String jvm;

        private SystemInfo( final String cpuModel, final int cores, final long memoryGiB, final String os, final String jvm )
        {
            this.cpuModel = cpuModel;
            this.cores = cores;
            this.memoryGiB = memoryGiB;
            this.os = os;
            this.jvm = jvm;
        }

        static SystemInfo collect()
        {
            final int cores = Runtime.getRuntime().availableProcessors();
            String cpuModel = "unknown";
            long memBytes = 0;

            final Path cpuinfo = Paths.get( "/proc/cpuinfo" );
            if ( Files.exists( cpuinfo ) )
            {
                try
                {
                    for ( final String line : Files.readAllLines( cpuinfo, StandardCharsets.UTF_8 ) )
                    {
                        if ( line.startsWith( "model name" ) )
                        {
                            cpuModel = line.substring( line.indexOf( ':' ) + 1 ).trim();
                            break;
                        }
                    }
                }
                catch ( IOException ignore )
                {
                }
            }

            final Path meminfo = Paths.get( "/proc/meminfo" );
            if ( Files.exists( meminfo ) )
            {
                try
                {
                    for ( final String line : Files.readAllLines( meminfo, StandardCharsets.UTF_8 ) )
                    {
                        if ( line.startsWith( "MemTotal:" ) )
                        {
                            final String[] parts = line.split( "\\s+" );
                            if ( parts.length >= 2 )
                            {
                                memBytes = Long.parseLong( parts[1] ) * 1024L;
                            }
                            break;
                        }
                    }
                }
                catch ( IOException ignore )
                {
                }
            }
            if ( memBytes == 0 )
            {
                memBytes = Runtime.getRuntime().maxMemory();
            }

            final String os = System.getProperty( "os.name" ) + " " + System.getProperty( "os.version" ) + " (" +
                System.getProperty( "os.arch" ) + ")";
            final String jvm = System.getProperty( "java.vm.name" ) + " " + System.getProperty( "java.runtime.version" );

            return new SystemInfo( cpuModel, cores, memBytes / ( 1024L * 1024L * 1024L ), os, jvm );
        }
    }
}
