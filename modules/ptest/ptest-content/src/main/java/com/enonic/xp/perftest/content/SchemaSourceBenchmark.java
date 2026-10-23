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
 * Reads of the same schema from the two sources an application is served from: a local application bundle ({@code local}) and the
 * nodes of a globally installed application ({@code persisted}). Nothing is written. The plain reads are what a running site does;
 * the {@code Recomputed} ones make the cache of the application stale before every read, so they measure reading and parsing the
 * resources of the source, with the form fragments inlined. Node reads per operation are printed at the end of every trial.
 */
@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MICROSECONDS )
@Warmup( iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS )
@Measurement( iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS )
@Fork( 1 )
@State( Scope.Benchmark )
public class SchemaSourceBenchmark
{
    @Param( {"local", "persisted"} )
    public String app;

    @Param( {"50", "500"} )
    public int contentTypes;

    private final SchemaFixture fixture = new SchemaFixture();

    private final LongAdder ops = new LongAdder();

    private Random rng;

    @Setup( Level.Trial )
    public void setUp()
        throws Exception
    {
        fixture.start( contentTypes, "local".equals( app ) );
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
     * A content type including two fragments read from the source: the content type and both fragments are read and parsed.
     */
    @Benchmark
    public ContentType getContentTypeWithFragmentsRecomputed()
    {
        ops.increment();
        fixture.invalidate();
        return getByName( fixture.typesWithFragments.get( rng.nextInt( fixture.typesWithFragments.size() ) ) );
    }

    /**
     * All content types of the application read from the source: the files listed, every content type and fragment read and parsed.
     */
    @Benchmark
    public ContentTypes getContentTypesByApplicationRecomputed()
    {
        ops.increment();
        fixture.invalidate();
        return fixture.contentTypeService.getByApplication( SchemaFixture.APP );
    }

    private ContentType getByName( final ContentTypeName name )
    {
        return fixture.contentTypeService.getByName( GetContentTypeParams.from( name ) );
    }

    @TearDown( Level.Trial )
    public void tearDown( final BenchmarkParams params )
        throws Exception
    {
        fixture.printNodeReads( params.getBenchmark(), "app=" + app + " contentTypes=" + contentTypes, ops.sum() );
        fixture.stop();
    }
}
