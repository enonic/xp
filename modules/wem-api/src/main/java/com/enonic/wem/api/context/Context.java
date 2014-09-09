package com.enonic.wem.api.context;

import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.repository.Repository;

public class Context
{
    private final Workspace workspace;

    private final Repository repository;

    private Context( Builder builder )
    {
        workspace = builder.workspace;
        repository = builder.repository;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }

    public Repository getRepository()
    {
        return repository;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Context ) )
        {
            return false;
        }

        final Context context = (Context) o;

        if ( workspace != null ? !workspace.equals( context.workspace ) : context.workspace != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return workspace != null ? workspace.hashCode() : 0;
    }

    public static final class Builder
    {
        private Workspace workspace;

        private Repository repository;

        private Builder()
        {
        }

        public Builder workspace( Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        public Builder repository( Repository repository )
        {
            this.repository = repository;
            return this;
        }

        public Context build()
        {
            return new Context( this );
        }
    }
}
