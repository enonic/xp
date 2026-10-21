package com.enonic.xp.core.impl.app;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.event.ApplicationEvents;
import com.enonic.xp.event.Event;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationGenerationTest
{
    private static final ApplicationKey MYAPP = ApplicationKey.from( "myapp" );

    private static final ApplicationKey OTHER = ApplicationKey.from( "other" );

    private final ApplicationGeneration generation = new ApplicationGeneration();

    @Test
    void starts_at_zero_and_moves_per_application()
    {
        assertEquals( 0, generation.of( MYAPP ) );

        generation.bump( MYAPP );
        generation.bump( MYAPP );

        assertEquals( 2, generation.of( MYAPP ) );
        assertEquals( 0, generation.of( OTHER ) );
    }

    @Test
    void updated_event_from_another_node_bumps()
    {
        generation.onEvent( Event.create( ApplicationEvents.updated( MYAPP, ApplicationEvents.KIND_CONTENT_TYPE, "myapp:article",
                                                                     ApplicationEvents.ACTION_UPDATED ) ).localOrigin( false ).build() );

        assertEquals( 1, generation.of( MYAPP ) );
        assertEquals( 0, generation.of( OTHER ) );
    }

    @Test
    void local_updated_event_is_ignored()
    {
        // the writer bumped the generation already, before publishing
        generation.onEvent( ApplicationEvents.updated( MYAPP, ApplicationEvents.KIND_PART, "myapp:hero", ApplicationEvents.ACTION_CREATED ) );

        assertEquals( 0, generation.of( MYAPP ) );
    }

    @Test
    void other_events_are_ignored()
    {
        generation.onEvent( Event.create( ApplicationEvents.installed( MYAPP ) ).localOrigin( false ).build() );
        generation.onEvent( Event.create( "node.updated" ).localOrigin( false ).value( ApplicationEvents.APPLICATION_KEY_KEY, MYAPP ).build() );

        assertEquals( 0, generation.of( MYAPP ) );
    }
}
