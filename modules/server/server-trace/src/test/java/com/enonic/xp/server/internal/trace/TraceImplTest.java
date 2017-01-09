package com.enonic.xp.server.internal.trace;

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.trace.TraceLocation;

import static org.junit.Assert.*;

public class TraceImplTest
{
    private TraceImpl trace;

    private TraceLocation location;

    @Before
    public void setup()
    {
        this.location = Mockito.mock( TraceLocation.class );
        this.trace = new TraceImpl( "name", "parentId", this.location );
    }

    @Test
    public void testGetters()
    {
        assertNotNull( this.trace.getId() );
        assertEquals( "parentId", this.trace.getParentId() );
        assertEquals( "name", this.trace.getName() );
        assertEquals( Duration.ZERO, this.trace.getDuration() );
        assertSame( this.location, this.trace.getLocation() );
        assertNull( this.trace.getStartTime() );
        assertNull( this.trace.getEndTime() );
        assertTrue( this.trace.inProgress() );
    }

    @Test
    public void testStartEnd()
    {
        this.trace.start();
        assertNotNull( this.trace.getStartTime() );
        assertNull( this.trace.getEndTime() );
        assertTrue( this.trace.getDuration().toMillis() >= 0 );
        assertTrue( this.trace.inProgress() );

        this.trace.end();
        assertNotNull( this.trace.getStartTime() );
        assertNotNull( this.trace.getEndTime() );
        assertTrue( this.trace.getDuration().toMillis() >= 0 );
        assertFalse( this.trace.inProgress() );
    }
}
