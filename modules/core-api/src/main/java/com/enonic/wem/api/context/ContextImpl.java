package com.enonic.wem.api.context;

import java.util.Map;
import java.util.concurrent.Callable;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.api.branch.Branch;

final class ContextImpl
    implements Context
{
    private final ImmutableMap<String, Object> attributes;

    private final LocalScope localScope;

    public ContextImpl( final ImmutableMap<String, Object> attributes, final LocalScope localScope )
    {
        this.attributes = attributes;
        this.localScope = localScope;
    }

    @Override
    public final RepositoryId getRepositoryId()
    {
        return getAttribute( RepositoryId.class );
    }

    @Override
    public final Branch getBranch()
    {
        return getAttribute( Branch.class );
    }

    @Override
    public final AuthenticationInfo getAuthInfo()
    {
        return getAttribute( AuthenticationInfo.class ) == null
            ? AuthenticationInfo.unAuthenticated()
            : getAttribute( AuthenticationInfo.class );
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> T getAttribute( final Class<T> type )
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
    public final void runWith( final Runnable runnable )
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
    public final <T> T callWith( final Callable<T> runnable )
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

