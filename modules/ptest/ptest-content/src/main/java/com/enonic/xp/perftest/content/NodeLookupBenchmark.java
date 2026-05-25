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

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MICROSECONDS )
@Warmup( iterations = 1, time = 2, timeUnit = TimeUnit.SECONDS )
@Measurement( iterations = 1, time = 10, timeUnit = TimeUnit.SECONDS )
@Fork( 1 )
@State( Scope.Benchmark )
public class NodeLookupBenchmark
{
    private static final int CORPUS_SIZE = 10_000;

    private Bootstrap bs;

    private List<NodeId> ids;

    private List<NodePath> paths;

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
            final Node n = bs.nodeService.create( CreateNodeParams.create()
                .data( new PropertyTree() )
                .name( "corpus-" + i )
                .parent( ContentConstants.CONTENT_ROOT_PATH )
                .build() );
            ids.add( n.id() );
            paths.add( n.path() );
        }

        // End of corpus build: force a refresh so all corpus entries are
        // searchable, then restore realistic refresh cadence for measurement.
        bs.refresh();
        bs.setRefreshInterval( "1s" );

        rng = new Random( 42L );
    }

    @TearDown( Level.Trial )
    public void tearDown()
    {
        bs.stop();
    }

    @Benchmark
    public Node getById()
    {
        return bs.nodeService.getById( ids.get( rng.nextInt( ids.size() ) ) );
    }

    @Benchmark
    public Node getByPath()
    {
        return bs.nodeService.getByPath( paths.get( rng.nextInt( paths.size() ) ) );
    }
}
