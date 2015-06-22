package com.enonic.xp.resource;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResourceProblemExceptionTest
{
    @Test
    public void testSimple()
    {
        final ResourceKey resource = ResourceKey.from( "mymodule:/test.js" );

        final ResourceProblemException.Builder builder = ResourceProblemException.newBuilder();
        builder.resource( resource );
        builder.lineNumber( 10 );

        final ResourceProblemException ex = builder.build();
        assertNotNull( ex );
        assertEquals( 10, ex.getLineNumber() );
        assertEquals( resource, ex.getResource() );
        assertNotNull( ex.getCallStack() );
        assertTrue( ex.getCallStack().isEmpty() );
        assertSame( ex, ex.getInnerError() );
        assertEquals( "Empty message in exception", ex.getMessage() );
    }

    @Test
    public void testMessage()
    {
        final ResourceProblemException.Builder builder = ResourceProblemException.newBuilder();
        builder.message( "A {problem} here" );

        final ResourceProblemException ex = builder.build();
        assertNotNull( ex );
        assertEquals( "A {problem} here", ex.getMessage() );
    }

    @Test
    public void testMessageWithArgs()
    {
        final ResourceProblemException.Builder builder = ResourceProblemException.newBuilder();
        builder.message( "A {0} here", "problem" );

        final ResourceProblemException ex = builder.build();
        assertNotNull( ex );
        assertEquals( "A problem here", ex.getMessage() );
    }

    @Test
    public void testCallStack()
    {
        final ResourceProblemException.Builder builder = ResourceProblemException.newBuilder();
        builder.callLine( "first", 1 );
        builder.callLine( "second", 2 );

        final ResourceProblemException ex = builder.build();
        assertNotNull( ex );
        assertNotNull( ex.getCallStack() );
        assertEquals( 2, ex.getCallStack().size() );
        assertEquals( "[first at line 1, second at line 2]", ex.getCallStack().toString() );
    }

    @Test
    public void testInnerError()
    {
        final ResourceProblemException.Builder builder1 = ResourceProblemException.newBuilder();
        ResourceProblemException cause1 = builder1.build();

        final Throwable cause2 = new Throwable( cause1 );

        final ResourceProblemException.Builder builder2 = ResourceProblemException.newBuilder();
        builder2.cause( cause2 );

        final ResourceProblemException ex = builder2.build();
        assertNotNull( ex );

        final ResourceProblemException inner = ex.getInnerError();
        assertSame( cause1, inner );
    }
}
