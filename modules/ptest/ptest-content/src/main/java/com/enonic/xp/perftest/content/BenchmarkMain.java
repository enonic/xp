package com.enonic.xp.perftest.content;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

public final class BenchmarkMain
{
    private static final int CORPUS = Integer.getInteger( "perftest.corpus", 10_000 );

    private static final int WARMUP = Integer.getInteger( "perftest.warmup", 1_000 );

    private static final int CREATE_ITERATIONS = Integer.getInteger( "perftest.create.iterations", 100_000 );

    private static final int LOOKUP_ITERATIONS = Integer.getInteger( "perftest.lookup.iterations", 10_000 );

    private static final long SEED = Long.getLong( "perftest.seed", 42L );

    public static void main( final String[] args )
        throws Exception
    {
        final Set<String> selected = selectedTests();
        System.out.println( "[ptest] tests: " + selected );

        final Bootstrap bs = new Bootstrap();
        try
        {
            System.out.println( "[ptest] starting embedded XP services..." );
            bs.start();

            final boolean needCorpus = selected.contains( "getById" ) || selected.contains( "getByPath" );

            List<ContentId> ids = List.of();
            List<ContentPath> paths = List.of();
            if ( needCorpus )
            {
                System.out.println( "[ptest] preparing corpus: " + CORPUS + " contents" );
                ids = new ArrayList<>( CORPUS );
                paths = new ArrayList<>( CORPUS );
                for ( int i = 0; i < CORPUS; i++ )
                {
                    final Content c = bs.contentService.create( folderParams( "corpus-" + i ) );
                    ids.add( c.getId() );
                    paths.add( c.getPath() );
                }
            }

            final List<Result> results = new ArrayList<>();

            if ( selected.contains( "create" ) )
            {
                results.add( benchmarkCreate( bs.contentService ) );
            }
            if ( selected.contains( "getById" ) )
            {
                results.add( benchmarkGetById( bs.contentService, ids ) );
            }
            if ( selected.contains( "getByPath" ) )
            {
                results.add( benchmarkGetByPath( bs.contentService, paths ) );
            }

            writeReport( results );
        }
        finally
        {
            bs.stop();
        }
    }

    private static Set<String> selectedTests()
    {
        final Set<String> all = Set.of( "create", "getById", "getByPath" );
        final String raw = System.getProperty( "perftest.tests", "" ).trim();
        if ( raw.isEmpty() || "all".equalsIgnoreCase( raw ) )
        {
            return all;
        }
        final Set<String> requested = Arrays.stream( raw.split( "," ) ).map( String::trim ).filter( s -> !s.isEmpty() )
            .collect( Collectors.toUnmodifiableSet() );
        for ( final String r : requested )
        {
            if ( !all.contains( r ) )
            {
                throw new IllegalArgumentException( "Unknown perftest: " + r + ". Valid: " + all );
            }
        }
        return requested;
    }

    private static Result benchmarkCreate( final ContentService svc )
    {
        System.out.println( "[ptest] create: warmup " + WARMUP );
        for ( int i = 0; i < WARMUP; i++ )
        {
            svc.create( folderParams( "create-warmup-" + i ) );
        }
        System.out.println( "[ptest] create: timing " + CREATE_ITERATIONS );
        final long t0 = System.nanoTime();
        for ( int i = 0; i < CREATE_ITERATIONS; i++ )
        {
            svc.create( folderParams( "create-perf-" + i ) );
        }
        return new Result( "ContentService.create (folder, empty data)", CREATE_ITERATIONS, System.nanoTime() - t0 );
    }

    private static Result benchmarkGetById( final ContentService svc, final List<ContentId> ids )
    {
        final Random rng = new Random( SEED );
        System.out.println( "[ptest] getById: warmup " + WARMUP );
        for ( int i = 0; i < WARMUP; i++ )
        {
            assertNonNull( svc.getById( ids.get( rng.nextInt( ids.size() ) ) ) );
        }
        System.out.println( "[ptest] getById: timing " + LOOKUP_ITERATIONS );
        final long t0 = System.nanoTime();
        for ( int i = 0; i < LOOKUP_ITERATIONS; i++ )
        {
            assertNonNull( svc.getById( ids.get( rng.nextInt( ids.size() ) ) ) );
        }
        return new Result( "ContentService.getById (random over corpus)", LOOKUP_ITERATIONS, System.nanoTime() - t0 );
    }

    private static Result benchmarkGetByPath( final ContentService svc, final List<ContentPath> paths )
    {
        final Random rng = new Random( SEED );
        System.out.println( "[ptest] getByPath: warmup " + WARMUP );
        for ( int i = 0; i < WARMUP; i++ )
        {
            assertNonNull( svc.getByPath( paths.get( rng.nextInt( paths.size() ) ) ) );
        }
        System.out.println( "[ptest] getByPath: timing " + LOOKUP_ITERATIONS );
        final long t0 = System.nanoTime();
        for ( int i = 0; i < LOOKUP_ITERATIONS; i++ )
        {
            assertNonNull( svc.getByPath( paths.get( rng.nextInt( paths.size() ) ) ) );
        }
        return new Result( "ContentService.getByPath (random over corpus)", LOOKUP_ITERATIONS, System.nanoTime() - t0 );
    }

    private static void assertNonNull( final Content content )
    {
        if ( content == null )
        {
            throw new AssertionError( "lookup returned null - corpus item missing" );
        }
    }

    private static CreateContentParams folderParams( final String name )
    {
        return CreateContentParams.create()
            .name( name )
            .displayName( name )
            .parent( ContentPath.ROOT )
            .contentData( new PropertyTree() )
            .type( ContentTypeName.folder() )
            .build();
    }

    private static void writeReport( final List<Result> results )
        throws IOException
    {
        final String dir = System.getProperty( "perftest.report.dir", "build/perftest-report" );
        final Path reportDir = Paths.get( dir );
        Files.createDirectories( reportDir );
        final Path report = reportDir.resolve( "results.md" );

        final StringBuilder md = new StringBuilder( 1024 );
        md.append( "## ContentService benchmarks\n\n" );
        md.append( "Run: " ).append( Instant.now() ).append( "\n\n" );
        md.append( "| Operation | Iterations | Total time | Throughput | Per op |\n" );
        md.append( "|---|---:|---:|---:|---:|\n" );
        for ( final Result r : results )
        {
            md.append( "| " ).append( r.label ).append( " | " ).append( r.iterations ).append( " | " );
            md.append( String.format( Locale.ROOT, "%.2f s | %.0f ops/s | %.1f us/op |%n", r.seconds(), r.opsPerSec(), r.usPerOp() ) );
        }
        md.append( "\n_Warmup: " ).append( WARMUP ).append( " iterations per test._\n" );
        md.append( "\n_JVM: " ).append( System.getProperty( "java.vm.name" ) ).append( " " )
            .append( System.getProperty( "java.runtime.version" ) ).append( "._\n" );

        Files.write( report, md.toString().getBytes( StandardCharsets.UTF_8 ) );
        System.out.println( "[ptest] report written to " + report.toAbsolutePath() );

        for ( final Result r : results )
        {
            System.out.println( String.format( Locale.ROOT, "[ptest] %s x %d  |  %.2f s  |  %.0f ops/s  |  %.1f us/op",
                                               r.label, r.iterations, r.seconds(), r.opsPerSec(), r.usPerOp() ) );
        }
    }

    private static final class Result
    {
        final String label;

        final int iterations;

        final long elapsedNanos;

        Result( final String label, final int iterations, final long elapsedNanos )
        {
            this.label = label;
            this.iterations = iterations;
            this.elapsedNanos = elapsedNanos;
        }

        double seconds()
        {
            return elapsedNanos / 1_000_000_000.0;
        }

        double opsPerSec()
        {
            return iterations / seconds();
        }

        double usPerOp()
        {
            return elapsedNanos / 1_000.0 / iterations;
        }
    }
}
