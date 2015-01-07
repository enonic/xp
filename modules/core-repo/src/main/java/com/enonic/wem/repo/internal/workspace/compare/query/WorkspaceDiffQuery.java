package com.enonic.wem.repo.internal.workspace.compare.query;

import com.enonic.wem.api.workspace.Workspace;

public class WorkspaceDiffQuery
{
    private final Workspace source;

    private final Workspace target;

    private WorkspaceDiffQuery( final Builder builder )
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

        public WorkspaceDiffQuery build()
        {
            return new WorkspaceDiffQuery( this );
        }
    }
}
