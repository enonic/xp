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
        md.append( "| Benchmark | Mode | Score | Unit | Throughput | Time/node | Alloc/op | Alloc/node | Peak live/op |\n" );
        md.append( "|---|---|---:|---|---:|---:|---:|---:|---:|\n" );
        for ( final Row r : rows )
        {
            md.append( "| " ).append( r.benchmark ).append( " | " ).append( r.mode ).append( " | " );
            md.append( String.format( Locale.ROOT, "%.2f", r.score ) ).append( " | " );
            md.append( r.unit ).append( " | " );
            md.append( r.throughput() ).append( " | " );
            md.append( r.timePerNode() ).append( " | " );
            md.append( bytes( r.bytesPerOp ) ).append( " | " );
            md.append( bytes( r.bytesPerNode() ) ).append( " | " );
            md.append( bytes( r.peakLiveBytesPerOp ) ).append( " |\n" );
        }
        md.append( "\nA whole-tree operation is one operation over many nodes, so its throughput is counted in nodes rather than in\n" );
        md.append( "operations. **Alloc** is what the operation churns - every node it rewrites is read, rebuilt and re-indexed, whatever\n" );
        md.append( "the shape of the walk. **Peak live** is what it holds: how far the surviving heap grows while it runs.\n" );
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
            // reported by the benchmark itself, as the thread that ran the operation counted it
            final JsonNode secondary = item.path( "secondaryMetrics" );
            rows.add( new Row( benchmark, mode, score, unit, kiB( secondary, "allocKiB" ), kiB( secondary, "peakLiveKiB" ),
                               count( secondary, "nodes" ) ) );
        }
        return rows;
    }

    /** A counter the benchmark reported in KiB per operation, as bytes, or NaN where this benchmark does not count it. */
    private static double kiB( final JsonNode secondaryMetrics, final String counter )
    {
        final JsonNode counted = secondaryMetrics.path( counter );
        return counted.isMissingNode() ? Double.NaN : counted.path( "score" ).asDouble() * 1024.0;
    }

    private static double count( final JsonNode secondaryMetrics, final String counter )
    {
        final JsonNode counted = secondaryMetrics.path( counter );
        return counted.isMissingNode() ? Double.NaN : counted.path( "score" ).asDouble();
    }

    private static String bytes( final double value )
    {
        if ( Double.isNaN( value ) )
        {
            return "-";
        }
        if ( value >= 1024.0 * 1024.0 )
        {
            return String.format( Locale.ROOT, "%.1f MiB", value / ( 1024.0 * 1024.0 ) );
        }
        if ( value >= 1024.0 )
        {
            return String.format( Locale.ROOT, "%.1f KiB", value / 1024.0 );
        }
        return String.format( Locale.ROOT, "%.0f B", value );
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

        final double bytesPerOp;

        final double peakLiveBytesPerOp;

        final double nodesPerOp;

        Row( final String benchmark, final String mode, final double score, final String unit, final double bytesPerOp,
             final double peakLiveBytesPerOp, final double nodesPerOp )
        {
            this.benchmark = benchmark;
            this.mode = mode;
            this.score = score;
            this.unit = unit;
            this.bytesPerOp = bytesPerOp;
            this.peakLiveBytesPerOp = peakLiveBytesPerOp;
            this.nodesPerOp = nodesPerOp;
        }

        double bytesPerNode()
        {
            return Double.isNaN( nodesPerOp ) || nodesPerOp <= 0 ? Double.NaN : bytesPerOp / nodesPerOp;
        }

        /**
         * Nodes per second where the benchmark says how many nodes one operation covers, operations per second otherwise. The reciprocal
         * of a whole-tree operation says nothing: it is one operation however many nodes it rewrote.
         */
        String throughput()
        {
            final double seconds = secondsPerOp();
            if ( Double.isNaN( seconds ) || seconds <= 0 )
            {
                return "-";
            }
            return Double.isNaN( nodesPerOp )
                ? String.format( Locale.ROOT, "%,.0f ops/s", 1.0 / seconds )
                : String.format( Locale.ROOT, "%,.0f nodes/s", nodesPerOp / seconds );
        }

        String timePerNode()
        {
            final double seconds = secondsPerOp();
            if ( Double.isNaN( seconds ) || Double.isNaN( nodesPerOp ) || nodesPerOp <= 0 )
            {
                return "-";
            }
            return String.format( Locale.ROOT, "%.1f us", seconds / nodesPerOp * 1_000_000.0 );
        }

        private double secondsPerOp()
        {
            switch ( unit )
            {
                case "ns/op":
                    return score / 1_000_000_000.0;
                case "us/op":
                    return score / 1_000_000.0;
                case "ms/op":
                    return score / 1_000.0;
                case "s/op":
                    return score;
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
