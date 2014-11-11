package com.enonic.wem.api.context;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.session.SessionKey;
import com.enonic.wem.api.session.SimpleSession;
import com.enonic.wem.api.workspace.Workspace;

import static org.junit.Assert.*;

public class ContextImplTest
{
    private final class SampleValue
    {
    }

    @Test
    public void testAttributeByKey()
    {
        final ContextImpl context = new ContextImpl();

        assertNull( context.getAttribute( "key1" ) );
        assertEquals( 0, context.getAttributes().size() );

        context.setAttribute( "key1", "value1" );
        assertEquals( "value1", context.getAttribute( "key1" ) );
        assertEquals( 1, context.getAttributes().size() );
    }

    @Test
    public void testAttributeByType()
    {
        final ContextImpl context = new ContextImpl();

        assertNull( context.getAttribute( SampleValue.class ) );
        assertEquals( 0, context.getAttributes().size() );

        final SampleValue value = new SampleValue();
        context.setAttribute( value );
        assertSame( value, context.getAttribute( SampleValue.class ) );
        assertEquals( 1, context.getAttributes().size() );
    }

    @Test
    public void testAttributeByKey_session()
    {
        final SimpleSession session = new SimpleSession( SessionKey.generate() );
        session.setAttribute( "key1", "value1" );

        final ContextImpl context = new ContextImpl();
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

        final ContextImpl context = new ContextImpl();
        context.setSession( session );
        assertSame( session, context.getSession() );

        assertSame( value, context.getAttribute( SampleValue.class ) );
        assertEquals( 0, context.getAttributes().size() );
    }

    @Test
    public void testRunWith()
    {
        final Context old = new ContextImpl();
        ContextAccessor.INSTANCE.set( old );

        final ContextImpl context = new ContextImpl();
        assertSame( old, ContextAccessor.current() );

        context.runWith( () -> assertSame( context, ContextAccessor.current() ) );

        assertSame( old, ContextAccessor.current() );
    }

    @Test
    public void testCallWith()
    {
        final Context old = new ContextImpl();
        ContextAccessor.INSTANCE.set( old );

        final ContextImpl context = new ContextImpl();
        assertSame( old, ContextAccessor.current() );

        final boolean result = context.callWith( () -> {
            assertSame( context, ContextAccessor.current() );
            return true;
        } );

        assertTrue( result );
    }

    @Test(expected = RuntimeException.class)
    public void testRunWith_runtimeException()
    {
        final ContextImpl context = new ContextImpl();
        context.callWith( () -> {
            throw new RuntimeException();
        } );
    }

    @Test(expected = IOException.class)
    public void testRunWith_checkedException()
    {
        final ContextImpl context = new ContextImpl();
        context.callWith( () -> {
            throw new IOException();
        } );
    }

    @Test
    public void testSetAttributes()
    {
        final ContextImpl context = new ContextImpl();

        assertNull( context.getAttribute( "key1" ) );
        assertEquals( 0, context.getAttributes().size() );

        final Map<String, Object> map = Maps.newHashMap();
        map.put( "key1", "value1" );
        context.setAttributes( map );

        assertEquals( "value1", context.getAttribute( "key1" ) );
        assertEquals( 1, context.getAttributes().size() );
    }

    @Test
    public void testRepositoryId()
    {
        final ContextImpl context = new ContextImpl();
        assertNull( context.getRepositoryId() );

        final RepositoryId value = RepositoryId.from( "repoId" );
        context.setAttribute( value );
        assertSame( value, context.getRepositoryId() );
    }

    @Test
    public void testWorkspace()
    {
        final ContextImpl context = new ContextImpl();
        assertNull( context.getWorkspace() );

        final Workspace value = Workspace.from( "workspace" );
        context.setAttribute( value );
        assertSame( value, context.getWorkspace() );
    }

    @Test
    public void testAuthInfo()
    {
        final ContextImpl context = new ContextImpl();
        assertNull( context.getAuthInfo() );

        final AuthenticationInfo value = AuthenticationInfo.failed();
        context.setAttribute( value );
        assertSame( value, context.getAuthInfo() );
    }
}
