package com.enonic.wem.core.workspace.query;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;

public class WorkspacePathsQuery
    extends AbstractWorkspaceQuery
{
    private final NodePaths nodePaths;

    private WorkspacePathsQuery( final Builder builder )
    {
        super( builder );
        nodePaths = builder.nodePaths;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public Set<String> getNodePathsAsStrings()
    {
        final Set<String> nodePathsAsStrings = Sets.newLinkedHashSet();

        for ( final NodePath path : this.nodePaths )
        {
            nodePathsAsStrings.add( path.toString() );
        }

        return nodePathsAsStrings;
    }

    public static final class Builder
        extends AbstractWorkspaceQuery.Builder<Builder>
    {
        private NodePaths nodePaths;

        private Builder()
        {
        }

        public Builder nodePaths( NodePaths nodePaths )
        {
            this.nodePaths = nodePaths;
            return this;
        }

        public WorkspacePathsQuery build()
        {
            return new WorkspacePathsQuery( this );
        }
    }
}

