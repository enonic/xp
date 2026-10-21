package com.enonic.xp.perftest.content;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.BenchmarkParams;

import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetContentTypeParams;

/**
 * Reads of the schema of an application persisted as nodes, the way the content and site services read it: content types with
 * their form fragments inlined, part descriptors, and the listing of the schema files; and a read right after a write of a
 * fragment it includes, with the write alone to subtract. Besides the time per operation, the number of node reads per
 * operation is printed at the end of every trial ({@code node reads/op}), which does not depend on the machine.
 */
@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MICROSECONDS )
@Warmup( iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS )
@Measurement( iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS )
@Fork( 1 )
@State( Scope.Benchmark )
public class SchemaLookupBenchmark
{
    @Param( {"50", "500"} )
    public int contentTypes;

    private final SchemaFixture fixture = new SchemaFixture();

    private final LongAdder ops = new LongAdder();

    private Random rng;

    @Setup( Level.Trial )
    public void setUp()
        throws Exception
    {
        fixture.start( contentTypes );
        rng = new Random( 42L );
    }

    @Benchmark
    public ContentType getContentTypeWithFragments()
    {
        ops.increment();
        return getByName( fixture.typesWithFragments.get( rng.nextInt( fixture.typesWithFragments.size() ) ) );
    }

    @Benchmark
    public ContentType getContentTypePlain()
    {
        ops.increment();
        return getByName( fixture.plainTypes.get( rng.nextInt( fixture.plainTypes.size() ) ) );
    }

    @Benchmark
    public ContentTypes getContentTypesByApplication()
    {
        ops.increment();
        return fixture.contentTypeService.getByApplication( SchemaFixture.APP );
    }

    @Benchmark
    public PartDescriptor getPart()
    {
        ops.increment();
        return fixture.partDescriptorService.getByKey( fixture.parts.get( rng.nextInt( fixture.parts.size() ) ) );
    }

    @Benchmark
    public ResourceKeys findContentTypeFiles()
    {
        ops.increment();
        return fixture.resourceService.findFiles( SchemaFixture.APP, "^/cms/content-types/.+\\.yaml$" );
    }

    /**
     * A fragment is rewritten, then a content type including it is read: the read misses with the cache.
     */
    @Benchmark
    public ContentType writeFragmentThenReadContentType()
    {
        ops.increment();
        fixture.writeFragment( 0 );
        // type0 includes fragment0 and fragment1
        return getByName( fixture.typesWithFragments.get( 0 ) );
    }

    /**
     * The write alone, to subtract from {@link #writeFragmentThenReadContentType()}.
     */
    @Benchmark
    public void writeFragment()
    {
        ops.increment();
        fixture.writeFragment( 0 );
    }

    private ContentType getByName( final ContentTypeName name )
    {
        return fixture.contentTypeService.getByName( GetContentTypeParams.from( name ) );
    }

    @TearDown( Level.Trial )
    public void tearDown( final BenchmarkParams params )
        throws Exception
    {
        fixture.printNodeReads( params.getBenchmark(), "contentTypes=" + contentTypes, ops.sum() );
        fixture.stop();
    }
}
