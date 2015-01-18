package com.enonic.wem.api.exception;

import org.junit.Test;

import static org.junit.Assert.*;

public class SystemExceptionTest
{
    @Test
    public void testSimple()
    {
        final SystemException ex1 = new SystemException( "Some {0} here", "message" );
        assertEquals( "Some message here", ex1.getMessage() );

        final Throwable cause = new RuntimeException();
        final SystemException ex2 = new SystemException( cause, "Some {0} here and {1}", "message", "there" );
        assertSame( cause, ex2.getCause() );
        assertEquals( "Some message here and there", ex2.getMessage() );
    }
}
