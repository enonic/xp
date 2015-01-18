package com.enonic.wem.api.context;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.workspace.Workspace;

public final class ContextBuilder
{
    private LocalScope localScope;

    private final ImmutableMap.Builder<String, Object> attributes;

    private ContextBuilder()
    {
        this.attributes = ImmutableMap.builder();
    }

    public ContextBuilder repositoryId( final String value )
    {
        return repositoryId( RepositoryId.from( value ) );
    }

    public ContextBuilder repositoryId( final RepositoryId value )
    {
        return attribute( value );
    }

    public ContextBuilder workspace( final String value )
    {
        return workspace( Workspace.from( value ) );
    }

    public ContextBuilder workspace( final Workspace value )
    {
        return attribute( value );
    }

    public ContextBuilder authInfo( final AuthenticationInfo value )
    {
        return attribute( value );
    }

    public ContextBuilder attribute( final String key, final Object value )
    {
        this.attributes.put( key, value );
        return this;
    }

    public <T> ContextBuilder attribute( final T value )
    {
        return attribute( value.getClass().getName(), value );
    }

    public Context build()
    {
        if ( this.localScope == null )
        {
            this.localScope = new LocalScopeImpl();
        }

        return new ContextImpl( this.attributes.build(), this.localScope );
    }

    public static ContextBuilder create()
    {
        return new ContextBuilder();
    }

    public static ContextBuilder from( final Context parent )
    {
        final ContextBuilder builder = new ContextBuilder();
        builder.localScope = parent.getLocalScope();
        builder.attributes.putAll( parent.getAttributes() );
        return builder;
    }
}
