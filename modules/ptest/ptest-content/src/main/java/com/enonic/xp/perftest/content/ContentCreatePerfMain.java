package com.enonic.xp.perftest.content;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Locale;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

public final class ContentCreatePerfMain
{
    private static final int WARMUP = Integer.getInteger( "perftest.warmup", 1_000 );

    private static final int N = Integer.getInteger( "perftest.iterations", 100_000 );

    public static void main( final String[] args )
        throws Exception
    {
        final Bootstrap bs = new Bootstrap();
        try
        {
            System.out.println( "[ptest] starting embedded XP services..." );
            bs.start();
            System.out.println( "[ptest] warmup: " + WARMUP + " creates" );
            for ( int i = 0; i < WARMUP; i++ )
            {
                bs.contentService.create( params( "warmup-" + i ) );
            }

            System.out.println( "[ptest] timing: " + N + " creates" );
            final long t0 = System.nanoTime();
            for ( int i = 0; i < N; i++ )
            {
                bs.contentService.create( params( "perf-" + i ) );
            }
            final long elapsed = System.nanoTime() - t0;

            final double seconds = elapsed / 1_000_000_000.0;
            final double opsPerSec = N / seconds;
            final double usPerOp = elapsed / 1_000.0 / N;

            final String line = String.format( Locale.ROOT,
                                               "ContentService.create x %d  |  %.2f s  |  %.0f ops/s  |  %.1f us/op",
                                               N, seconds, opsPerSec, usPerOp );
            System.out.println( "[ptest] " + line );

            writeReport( seconds, opsPerSec, usPerOp );
        }
        finally
        {
            bs.stop();
        }
    }

    private static CreateContentParams params( final String name )
    {
        return CreateContentParams.create()
            .name( name )
            .displayName( name )
            .parent( ContentPath.ROOT )
            .contentData( new PropertyTree() )
            .type( ContentTypeName.folder() )
            .build();
    }

    private static void writeReport( final double seconds, final double opsPerSec, final double usPerOp )
        throws IOException
    {
        final String dir = System.getProperty( "perftest.report.dir", "build/perftest-report" );
        final Path reportDir = Paths.get( dir );
        Files.createDirectories( reportDir );
        final Path report = reportDir.resolve( "results.md" );

        final StringBuilder md = new StringBuilder( 512 );
        md.append( "## ContentService.create benchmark\n\n" );
        md.append( "Run: " ).append( Instant.now() ).append( "\n\n" );
        md.append( "| Operation | Iterations | Total time | Throughput | Per op |\n" );
        md.append( "|---|---:|---:|---:|---:|\n" );
        md.append( "| `ContentService.create` (folder, empty data) | " ).append( N ).append( " | " );
        md.append( String.format( Locale.ROOT, "%.2f s | %.0f ops/s | %.1f us/op |\n", seconds, opsPerSec, usPerOp ) );
        md.append( "\n_Warmup: " ).append( WARMUP ).append( " iterations._\n" );
        md.append( "\n_JVM: " ).append( System.getProperty( "java.vm.name" ) ).append( " " )
            .append( System.getProperty( "java.runtime.version" ) ).append( "._\n" );

        Files.write( report, md.toString().getBytes( StandardCharsets.UTF_8 ) );
        System.out.println( "[ptest] report written to " + report.toAbsolutePath() );
    }
}
