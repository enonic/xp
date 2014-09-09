package com.enonic.wem.core.workspace.query;

import com.enonic.wem.api.entity.NodePath;

public class WorkspacePathQuery
    extends AbstractWorkspaceQuery
{
    private NodePath nodePath;

    private WorkspacePathQuery( Builder builder )
    {
        super( builder );
        nodePath = builder.nodePath;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public String getNodePathAsString()
    {
        return nodePath.toString();
    }


    public static final class Builder
        extends AbstractWorkspaceQuery.Builder<Builder>
    {
        private NodePath nodePath;

        private Builder()
        {
        }

        public Builder nodePath( NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public WorkspacePathQuery build()
        {
            return new WorkspacePathQuery( this );
        }
    }
}
