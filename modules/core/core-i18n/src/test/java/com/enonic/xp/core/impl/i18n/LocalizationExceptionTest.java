package com.enonic.xp.core.impl.i18n;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.*;

public class LocalizationExceptionTest
{
    @Test
    public void testException()
    {
        final IOException cause = new IOException();
        final LocalizationException ex = new LocalizationException( "test", cause );
        assertEquals( "test", ex.getMessage() );
        assertSame( cause, ex.getCause() );
    }
}
