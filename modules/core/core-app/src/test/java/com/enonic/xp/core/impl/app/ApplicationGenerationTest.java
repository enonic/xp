package com.enonic.xp.core.impl.app;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.event.ApplicationEvents;
import com.enonic.xp.event.Event;
import com.enonic.xp.node.NodeVersionId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationGenerationTest
{
    private static final ApplicationKey MYAPP = ApplicationKey.from( "myapp" );

    private static final ApplicationKey OTHER = ApplicationKey.from( "other" );

    private final Map<ApplicationKey, NodeVersionId> versions = new HashMap<>();

    private final AtomicInteger reads = new AtomicInteger();

    private final AtomicLong now = new AtomicLong( 123_456_789 );

    private RuntimeException readFailure;

    private final ApplicationGeneration generation = generation( 1000 );

    private ApplicationGeneration generation( final long checkIntervalMillis )
    {
        return new ApplicationGeneration( key -> {
            reads.incrementAndGet();
            if ( readFailure != null )
            {
                throw readFailure;
            }
            return versions.get( key );
        }, now::get, checkIntervalMillis );
    }

    private void advance( final long millis )
    {
        now.addAndGet( Duration.ofMillis( millis ).toNanos() );
    }

    @Test
    void moves_per_application()
    {
        final long myapp = generation.of( MYAPP );
        final long other = generation.of( OTHER );

        generation.bump( MYAPP );
        generation.bump( MYAPP );

        assertEquals( myapp + 2, generation.of( MYAPP ) );
        assertEquals( other, generation.of( OTHER ) );
    }

    @Test
    void updated_event_from_another_node_bumps()
    {
        final long myapp = generation.of( MYAPP );
        final long other = generation.of( OTHER );

        generation.onEvent( Event.create( ApplicationEvents.updated( MYAPP, ApplicationEvents.KIND_CONTENT_TYPE, "myapp:article",
                                                                     ApplicationEvents.ACTION_UPDATED ) ).localOrigin( false ).build() );

        assertEquals( myapp + 1, generation.of( MYAPP ) );
        assertEquals( other, generation.of( OTHER ) );
    }

    @Test
    void local_updated_event_is_ignored()
    {
        final long myapp = generation.of( MYAPP );

        // the writer bumped the generation already, before publishing
        generation.onEvent( ApplicationEvents.updated( MYAPP, ApplicationEvents.KIND_PART, "myapp:hero", ApplicationEvents.ACTION_CREATED ) );

        assertEquals( myapp, generation.of( MYAPP ) );
    }

    @Test
    void other_events_are_ignored()
    {
        final long myapp = generation.of( MYAPP );

        generation.onEvent( Event.create( ApplicationEvents.installed( MYAPP ) ).localOrigin( false ).build() );
        generation.onEvent( Event.create( "node.updated" ).localOrigin( false ).value( ApplicationEvents.APPLICATION_KEY_KEY, MYAPP ).build() );

        assertEquals( myapp, generation.of( MYAPP ) );
    }

    @Test
    void version_is_read_on_first_use_and_then_once_per_interval()
    {
        generation.of( MYAPP );
        generation.of( MYAPP );
        assertEquals( 1, reads.get() );

        advance( 999 );
        generation.of( MYAPP );
        assertEquals( 1, reads.get() );

        advance( 1 );
        generation.of( MYAPP );
        generation.of( MYAPP );
        assertEquals( 2, reads.get() );
    }

    @Test
    void nothing_is_read_without_use()
    {
        advance( 60_000 );

        assertEquals( 0, reads.get() );
    }

    @Test
    void version_moved_without_event_is_seen_after_the_interval()
    {
        versions.put( MYAPP, NodeVersionId.from( "v1" ) );
        final long before = generation.of( MYAPP );

        // written on another node, the event lost
        versions.put( MYAPP, NodeVersionId.from( "v2" ) );

        assertEquals( before, generation.of( MYAPP ) );

        advance( 1000 );

        assertEquals( before + 1, generation.of( MYAPP ) );
        assertEquals( before + 1, generation.of( MYAPP ) );
    }

    @Test
    void same_version_keeps_the_generation()
    {
        versions.put( MYAPP, NodeVersionId.from( "v1" ) );
        final long before = generation.of( MYAPP );

        advance( 5000 );

        assertEquals( before, generation.of( MYAPP ) );
        assertEquals( 2, reads.get() );
    }

    @Test
    void application_node_removed_moves_the_generation()
    {
        versions.put( MYAPP, NodeVersionId.from( "v1" ) );
        final long before = generation.of( MYAPP );

        versions.remove( MYAPP );
        advance( 1000 );

        assertEquals( before + 1, generation.of( MYAPP ) );
    }

    @Test
    void first_version_read_moves_the_generation()
    {
        // a value computed before the version is first known may predate it
        generation.bump( MYAPP );

        assertEquals( 2, generation.of( MYAPP ) );
    }

    @Test
    void failed_read_keeps_the_generation_and_is_retried_after_the_interval()
    {
        versions.put( MYAPP, NodeVersionId.from( "v1" ) );
        final long before = generation.of( MYAPP );

        versions.put( MYAPP, NodeVersionId.from( "v2" ) );
        readFailure = new IllegalStateException( "repository unavailable" );
        advance( 1000 );

        assertEquals( before, generation.of( MYAPP ) );
        assertEquals( before, generation.of( MYAPP ) );
        assertEquals( 2, reads.get() );

        readFailure = null;
        advance( 1000 );

        assertEquals( before + 1, generation.of( MYAPP ) );
        assertEquals( 3, reads.get() );
    }

    @Test
    void zero_interval_reads_on_every_use()
    {
        final ApplicationGeneration strict = generation( 0 );
        versions.put( MYAPP, NodeVersionId.from( "v1" ) );
        final long before = strict.of( MYAPP );

        versions.put( MYAPP, NodeVersionId.from( "v2" ) );

        assertEquals( before + 1, strict.of( MYAPP ) );
        assertEquals( 2, reads.get() );
    }
}
