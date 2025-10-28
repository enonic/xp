package com.enonic.xp.core.impl.i18n;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class LocalizationExceptionTest
{
    @Test
    void testException()
    {
        final IOException cause = new IOException();
        final LocalizationException ex = new LocalizationException( "test", cause );
        assertEquals( "test", ex.getMessage() );
        assertSame( cause, ex.getCause() );
    }
}
