package com.enonic.xp.perftest.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MICROSECONDS )
@Warmup( iterations = 1, time = 2, timeUnit = TimeUnit.SECONDS )
@Measurement( iterations = 1, time = 10, timeUnit = TimeUnit.SECONDS )
@Fork( 1 )
@State( Scope.Benchmark )
public class ContentLookupBenchmark
{
    private static final int CORPUS_SIZE = 10_000;

    private Bootstrap bs;

    private List<ContentId> ids;

    private List<ContentPath> paths;

    private Random rng;

    @Setup( Level.Trial )
    public void setUp()
        throws Exception
    {
        bs = new Bootstrap();
        bs.start();

        // Fast corpus prep (refresh disabled).
        bs.setRefreshInterval( "-1" );

        ids = new ArrayList<>( CORPUS_SIZE );
        paths = new ArrayList<>( CORPUS_SIZE );
        for ( int i = 0; i < CORPUS_SIZE; i++ )
        {
            final Content c = bs.contentService.create( CreateContentParams.create()
                .name( "corpus-" + i )
                .displayName( "corpus" )
                .parent( ContentPath.ROOT )
                .contentData( new PropertyTree() )
                .type( ContentTypeName.folder() )
                .build() );
            ids.add( c.getId() );
            paths.add( c.getPath() );
        }

        // End of corpus build: force a refresh so all corpus entries are
        // searchable, then restore production-like settings for measurement.
        bs.refresh();
        bs.setRefreshInterval( "1s" );
        bs.setStoreThrottleType( "merge" );

        rng = new Random( 42L );
    }

    @TearDown( Level.Trial )
    public void tearDown()
    {
        bs.stop();
    }

    @Benchmark
    public Content getById()
    {
        return bs.contentService.getById( ids.get( rng.nextInt( ids.size() ) ) );
    }

    @Benchmark
    public Content getByPath()
    {
        return bs.contentService.getByPath( paths.get( rng.nextInt( paths.size() ) ) );
    }
}
