package com.enonic.xp.server.internal.trace;

import org.junit.jupiter.api.Test;

import com.enonic.xp.trace.TraceLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TraceLocationImplTest
{
    @Test
    void testLocation()
    {
        final TraceLocation location = TraceLocationImpl.findLocation();
        assertNotNull( location );
        assertEquals( getClass().getName(), location.getClassName() );
        assertEquals( "testLocation", location.getMethod() );
        assertEquals( 15, location.getLineNumber() );
        assertEquals( getClass().getName() + ".testLocation:15", location.toString() );
    }
}
