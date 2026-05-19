package com.enonic.xp.context;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContextImplTest
{
    private static final class SampleValue
    {
    }

    private ContextImpl createContext()
    {
        return new ContextImpl( ImmutableMap.of(), new LocalScopeImpl() );
    }

    private ContextImpl createContext( final String key, final Object value )
    {
        return new ContextImpl( ImmutableMap.of( key, value ), new LocalScopeImpl() );
    }

    @Test
    void testAttributeByKey()
    {
        final ContextImpl context = createContext( "key1", "value1" );
        assertEquals( "value1", context.getAttribute( "key1" ) );
        assertEquals( 1, context.getAttributes().size() );
    }

    @Test
    void testAttributeByKey_foundInLocal()
    {
        final ContextImpl context = createContext();
        context.getLocalScope().setAttribute( "key1", "value1" );

        assertEquals( "value1", context.getAttribute( "key1" ) );
        assertEquals( 0, context.getAttributes().size() );
    }

    @Test
    void testAttributeByType()
    {
        final SampleValue value = new SampleValue();
        final ContextImpl context = createContext( value.getClass().getName(), value );

        assertSame( value, context.getAttribute( SampleValue.class ) );
        assertEquals( 1, context.getAttributes().size() );
    }

    @Test
    void testRunWith()
    {
        final Context old = createContext();
        ContextAccessor.INSTANCE.set( old );

        final ContextImpl context = createContext();
        assertSame( old, ContextAccessor.current() );

        context.runWith( () -> assertSame( context, ContextAccessor.current() ) );

        assertSame( old, ContextAccessor.current() );
    }

    @Test
    void testCallWith()
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

    @Test
    void testRunWith_runtimeException()
    {
        final ContextImpl context = createContext();

        assertThrows(RuntimeException.class, () ->
        context.callWith( () -> {
            throw new RuntimeException();
        } ) );
    }

    @Test
    void testRunWith_checkedException()
    {
        final ContextImpl context = createContext();

        assertThrows(IOException.class, () ->
        context.callWith( () -> {
            throw new IOException();
        } ) );
    }

    @Test
    void testRepositoryId()
    {
        final ContextImpl context = createContext();
        assertNull( context.getRepositoryId() );

        final RepositoryId value = RepositoryId.from( "repo-id" );
        context.getLocalScope().setAttribute( value );
        assertSame( value, context.getRepositoryId() );
    }

    @Test
    void testBranch()
    {
        final ContextImpl context = createContext();
        assertNull( context.getBranch() );

        final Branch value = Branch.from( "branch" );
        context.getLocalScope().setAttribute( value );
        assertSame( value, context.getBranch() );
    }

    @Test
    void testAuthInfo()
    {
        final ContextImpl context = createContext();
        assertEquals( AuthenticationInfo.unAuthenticated(), context.getAuthInfo() );

        final AuthenticationInfo value = AuthenticationInfo.unAuthenticated();
        context.getLocalScope().setAttribute( value );
        assertSame( value, context.getAuthInfo() );
    }
}
