package com.enonic.wem.api.context;

import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.session.Session;
import com.enonic.wem.api.workspace.Workspace;

public final class ContextBuilder
{
    private final ContextImpl context;

    private ContextBuilder()
    {
        this.context = new ContextImpl();
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

    public ContextBuilder session( final Session session )
    {
        this.context.setSession( session );
        return this;
    }

    public ContextBuilder authInfo( final AuthenticationInfo value )
    {
        return attribute( value );
    }

    public ContextBuilder attribute( final String key, final Object value )
    {
        this.context.setAttribute( key, value );
        return this;
    }

    public <T> ContextBuilder attribute( final T value )
    {
        this.context.setAttribute( value );
        return this;
    }

    public Context build()
    {
        return this.context;
    }

    public static ContextBuilder create()
    {
        return new ContextBuilder();
    }

    public static ContextBuilder from( final Context context )
    {
        final ContextBuilder builder = new ContextBuilder();
        builder.context.setSession( context.getSession() );
        builder.context.setAttributes( context.getAttributes() );
        return builder;
    }
}
