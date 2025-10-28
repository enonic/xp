package com.enonic.xp.context;

import org.junit.jupiter.api.Test;

import com.enonic.xp.session.Session;
import com.enonic.xp.session.SessionMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class LocalScopeImplTest
{
    private static final class SampleValue
    {
    }

    @Test
    void testAttributeByKey()
    {
        final LocalScopeImpl context = new LocalScopeImpl();

        assertNull( context.getAttribute( "key1" ) );
        assertEquals( 0, context.getAttributes().size() );

        context.setAttribute( "key1", "value1" );
        assertEquals( "value1", context.getAttribute( "key1" ) );
        assertEquals( 1, context.getAttributes().size() );

        context.removeAttribute( "key1" );
        assertNull( context.getAttribute( "key1" ) );
        assertEquals( 0, context.getAttributes().size() );
    }

    @Test
    void testAttributeByType()
    {
        final LocalScopeImpl context = new LocalScopeImpl();

        assertNull( context.getAttribute( SampleValue.class ) );
        assertEquals( 0, context.getAttributes().size() );

        final SampleValue value = new SampleValue();
        context.setAttribute( value );
        assertSame( value, context.getAttribute( SampleValue.class ) );
        assertEquals( 1, context.getAttributes().size() );

        context.removeAttribute( SampleValue.class );
        assertNull( context.getAttribute( SampleValue.class ) );
        assertEquals( 0, context.getAttributes().size() );
    }

    @Test
    void testAttributeByKey_session()
    {
        final Session session = new SessionMock();
        session.setAttribute( "key1", "value1" );

        final LocalScopeImpl context = new LocalScopeImpl();
        context.setSession( session );
        assertSame( session, context.getSession() );

        assertEquals( "value1", context.getAttribute( "key1" ) );
        assertEquals( 0, context.getAttributes().size() );
    }

    @Test
    void testAttributeByType_session()
    {
        final SampleValue value = new SampleValue();
        final Session session = new SessionMock();
        session.setAttribute( value );

        final LocalScopeImpl context = new LocalScopeImpl();
        context.setSession( session );
        assertSame( session, context.getSession() );

        assertSame( value, context.getAttribute( SampleValue.class ) );
        assertEquals( 0, context.getAttributes().size() );
    }
}
