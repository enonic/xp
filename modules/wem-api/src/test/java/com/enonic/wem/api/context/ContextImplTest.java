package com.enonic.wem.api.context;

import java.io.IOException;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.workspace.Workspace;

import static org.junit.Assert.*;

public class ContextImplTest
{
    private final class SampleValue
    {
    }

    private ContextImpl createContext()
    {
        final ImmutableMap<String, Object> map = ImmutableMap.of();
        return new ContextImpl( map, new LocalScopeImpl() );
    }

    private ContextImpl createContext( final String key, final Object value )
    {
        final ImmutableMap<String, Object> map = ImmutableMap.of( key, value );
        return new ContextImpl( map, new LocalScopeImpl() );
    }

    @Test
    public void testAttributeByKey()
    {
        final ContextImpl context = createContext( "key1", "value1" );
        assertEquals( "value1", context.getAttribute( "key1" ) );
        assertEquals( 1, context.getAttributes().size() );
    }

    @Test
    public void testAttributeByKey_foundInLocal()
    {
        final ContextImpl context = createContext();
        context.getLocalScope().setAttribute( "key1", "value1" );

        assertEquals( "value1", context.getAttribute( "key1" ) );
        assertEquals( 0, context.getAttributes().size() );
    }

    @Test
    public void testAttributeByType()
    {
        final SampleValue value = new SampleValue();
        final ContextImpl context = createContext( value.getClass().getName(), value );

        assertSame( value, context.getAttribute( SampleValue.class ) );
        assertEquals( 1, context.getAttributes().size() );
    }

    @Test
    public void testRunWith()
    {
        final Context old = createContext();
        ContextAccessor.INSTANCE.set( old );

        final ContextImpl context = createContext();
        assertSame( old, ContextAccessor.current() );

        context.runWith( () -> assertSame( context, ContextAccessor.current() ) );

        assertSame( old, ContextAccessor.current() );
    }

    @Test
    public void testCallWith()
    {
        final Context old = createContext();
        ContextAccessor.INSTANCE.set( old );

        final ContextImpl context = createContext();
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
        final ContextImpl context = createContext();
        context.callWith( () -> {
            throw new RuntimeException();
        } );
    }

    @Test(expected = IOException.class)
    public void testRunWith_checkedException()
    {
        final ContextImpl context = createContext();
        context.callWith( () -> {
            throw new IOException();
        } );
    }

    @Test
    public void testRepositoryId()
    {
        final ContextImpl context = createContext();
        assertNull( context.getRepositoryId() );

        final RepositoryId value = RepositoryId.from( "repoId" );
        context.getLocalScope().setAttribute( value );
        assertSame( value, context.getRepositoryId() );
    }

    @Test
    public void testWorkspace()
    {
        final ContextImpl context = createContext();
        assertNull( context.getWorkspace() );

        final Workspace value = Workspace.from( "workspace" );
        context.getLocalScope().setAttribute( value );
        assertSame( value, context.getWorkspace() );
    }

    @Test
    public void testAuthInfo()
    {
        final ContextImpl context = createContext();
        assertEquals( AuthenticationInfo.unAuthenticated(), context.getAuthInfo() );

        final AuthenticationInfo value = AuthenticationInfo.unAuthenticated();
        context.getLocalScope().setAttribute( value );
        assertSame( value, context.getAuthInfo() );
    }
}
