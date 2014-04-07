package com.enonic.wem.core.module.source;

import org.junit.Test;

import com.enonic.wem.api.module.ModuleResourceKey;

import static org.junit.Assert.*;

public class SourceNotFoundExceptionTest
{
    @Test
    public void testException()
    {
        final ModuleResourceKey key = ModuleResourceKey.from( "mymodule-1.0.0:test.js" );
        final SourceNotFoundException ex = new SourceNotFoundException( key );

        assertEquals( key, ex.getResource() );
        assertEquals( "mymodule-1.0.0:test.js not found", ex.getMessage() );
    }
}

