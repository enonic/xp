package com.enonic.wem.core.workspace.compare.query;

import com.enonic.wem.api.workspace.Workspace;

public class CompareWorkspacesQuery
{
    private final Workspace source;

    private final Workspace target;

    private CompareWorkspacesQuery( final Builder builder )
    {
        source = builder.source;
        target = builder.target;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Workspace getSource()
    {
        return source;
    }

    public Workspace getTarget()
    {
        return target;
    }

    public static final class Builder
    {
        private Workspace source;

        private Workspace target;

        public Builder source( Workspace source )
        {
            this.source = source;
            return this;
        }

        public Builder target( Workspace target )
        {
            this.target = target;
            return this;
        }

        public CompareWorkspacesQuery build()
        {
            return new CompareWorkspacesQuery( this );
        }
    }
}
