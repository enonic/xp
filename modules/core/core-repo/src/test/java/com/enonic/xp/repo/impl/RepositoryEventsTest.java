package com.enonic.xp.repo.impl;

import org.junit.Test;

import com.enonic.xp.event.Event;

import static org.junit.Assert.*;

public class RepositoryEventsTest
{
    @Test
    public void testRestored()
    {
        Event event = RepositoryEvents.restored();

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( RepositoryEvents.REPOSITORY_RESTORED_EVENT, event.getType() );
    }
}
