package com.enonic.xp.context;

import java.util.Map;
import java.util.concurrent.Callable;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.Exceptions;

final class ContextImpl
    implements Context
{
    private final ImmutableMap<String, Object> attributes;

    private final LocalScope localScope;

    ContextImpl( final ImmutableMap<String, Object> attributes, final LocalScope localScope )
    {
        this.attributes = attributes;
        this.localScope = localScope;
    }

    @Override
    public RepositoryId getRepositoryId()
    {
        return getAttribute( RepositoryId.class );
    }

    @Override
    public Branch getBranch()
    {
        return getAttribute( Branch.class );
    }

    @Override
    public AuthenticationInfo getAuthInfo()
    {
        final AuthenticationInfo attribute = getAttribute( AuthenticationInfo.class );
        return attribute == null ? AuthenticationInfo.unAuthenticated() : attribute;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttribute( final Class<T> type )
    {
        return (T) getAttribute( type.getName() );
    }

    @Override
    public LocalScope getLocalScope()
    {
        return this.localScope;
    }

    @Override
    public Object getAttribute( final String key )
    {
        final Object value = this.attributes.get( key );
        if ( value != null )
        {
            return value;
        }

        return this.localScope.getAttribute( key );
    }

    @Override
    public Map<String, Object> getAttributes()
    {
        return this.attributes;
    }

    @Override
    public void runWith( final Runnable runnable )
    {
        final Context old = ContextAccessor.INSTANCE.get();
        ContextAccessor.INSTANCE.set( this );

        try
        {
            runnable.run();
        }
        finally
        {
            ContextAccessor.INSTANCE.set( old );
        }
    }

    @Override
    public <T> T callWith( final Callable<T> runnable )
    {
        final Context old = ContextAccessor.INSTANCE.get();
        ContextAccessor.INSTANCE.set( this );

        try
        {
            return runnable.call();
        }
        catch ( final RuntimeException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
        finally
        {
            ContextAccessor.INSTANCE.set( old );
        }
    }
}

