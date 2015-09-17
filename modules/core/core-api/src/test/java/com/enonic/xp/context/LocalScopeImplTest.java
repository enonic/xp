package com.enonic.xp.context;

import org.junit.Test;

import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;

import static org.junit.Assert.*;

public class LocalScopeImplTest
{
    private final class SampleValue
    {
    }

    @Test
    public void testAttributeByKey()
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
    public void testAttributeByType()
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
    public void testAttributeByKey_session()
    {
        final SimpleSession session = new SimpleSession( SessionKey.generate() );
        session.setAttribute( "key1", "value1" );

        final LocalScopeImpl context = new LocalScopeImpl();
        context.setSession( session );
        assertSame( session, context.getSession() );

        assertEquals( "value1", context.getAttribute( "key1" ) );
        assertEquals( 0, context.getAttributes().size() );
    }

    @Test
    public void testAttributeByType_session()
    {
        final SampleValue value = new SampleValue();
        final SimpleSession session = new SimpleSession( SessionKey.generate() );
        session.setAttribute( value );

        final LocalScopeImpl context = new LocalScopeImpl();
        context.setSession( session );
        assertSame( session, context.getSession() );

        assertSame( value, context.getAttribute( SampleValue.class ) );
        assertEquals( 0, context.getAttributes().size() );
    }
}
