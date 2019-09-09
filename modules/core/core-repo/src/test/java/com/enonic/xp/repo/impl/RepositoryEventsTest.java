package com.enonic.xp.repo.impl;

import org.junit.jupiter.api.Test;

import com.enonic.xp.event.Event;

import static org.junit.jupiter.api.Assertions.*;

public class RepositoryEventsTest
{
    @Test
    public void testRestored()
    {
        Event event = RepositoryEvents.restored();

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( RepositoryEvents.RESTORED_EVENT_TYPE, event.getType() );
    }
}
