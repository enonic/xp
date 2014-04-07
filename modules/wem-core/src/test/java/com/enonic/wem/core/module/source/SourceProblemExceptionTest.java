package com.enonic.wem.core.module.source;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class SourceProblemExceptionTest
{
    @Test
    public void testSimple()
    {
        final ModuleSource source = Mockito.mock( ModuleSource.class );

        final SourceProblemException.Builder builder = SourceProblemException.newBuilder();
        builder.source( source );
        builder.lineNumber( 10 );

        final SourceProblemException ex = builder.build();
        assertNotNull( ex );
        assertEquals( 10, ex.getLineNumber() );
        assertSame( source, ex.getSource() );
        assertNotNull( ex.getCallStack() );
        assertTrue( ex.getCallStack().isEmpty() );
        assertSame( ex, ex.getInnerError() );
        assertEquals( "Empty message in exception", ex.getMessage() );
    }

    @Test
    public void testMessage()
    {
        final SourceProblemException.Builder builder = SourceProblemException.newBuilder();
        builder.message( "A {problem} here" );

        final SourceProblemException ex = builder.build();
        assertNotNull( ex );
        assertEquals( "A {problem} here", ex.getMessage() );
    }

    @Test
    public void testMessageWithArgs()
    {
        final SourceProblemException.Builder builder = SourceProblemException.newBuilder();
        builder.message( "A {0} here", "problem" );

        final SourceProblemException ex = builder.build();
        assertNotNull( ex );
        assertEquals( "A problem here", ex.getMessage() );
    }

    @Test
    public void testCallStack()
    {
        final SourceProblemException.Builder builder = SourceProblemException.newBuilder();
        builder.callLine( "first", 1 );
        builder.callLine( "second", 2 );

        final SourceProblemException ex = builder.build();
        assertNotNull( ex );
        assertNotNull( ex.getCallStack() );
        assertEquals( 2, ex.getCallStack().size() );
        assertEquals( "[first at line 1, second at line 2]", ex.getCallStack().toString() );
    }

    @Test
    public void testInnerError()
    {
        final SourceProblemException.Builder builder1 = SourceProblemException.newBuilder();
        SourceProblemException cause1 = builder1.build();

        final Throwable cause2 = new Throwable( cause1 );

        final SourceProblemException.Builder builder2 = SourceProblemException.newBuilder();
        builder2.cause( cause2 );

        final SourceProblemException ex = builder2.build();
        assertNotNull( ex );
        assertSame( cause1, ex.getInnerError() );
    }
}
