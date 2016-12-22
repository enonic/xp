package com.enonic.xp.server.internal.trace;

import org.junit.Test;

import com.enonic.xp.trace.TraceLocation;

import static org.junit.Assert.*;

public class TraceLocationImplTest
{
    @Test
    public void testLocation()
    {
        final TraceLocation location = TraceLocationImpl.findLocation();
        assertNotNull( location );
        assertEquals( getClass().getName(), location.getClassName() );
        assertEquals( "testLocation", location.getMethod() );
        assertEquals( 14, location.getLineNumber() );
        assertEquals( getClass().getName() + ".testLocation:14", location.toString() );
    }
}
