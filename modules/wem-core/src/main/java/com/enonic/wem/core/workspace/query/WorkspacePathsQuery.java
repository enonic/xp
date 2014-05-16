package com.enonic.wem.core.workspace.query;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.Workspace;

public class WorkspacePathsQuery
    extends AbstractWorkspaceQuery
{
    private final NodePaths nodePaths;

    public WorkspacePathsQuery( final Workspace workspace, final NodePaths nodePaths )
    {
        super( workspace );
        this.nodePaths = nodePaths;
    }

    public Set<String> getNodePathsAsStrings()
    {
        final Set<String> nodePathsAsStrings = Sets.newHashSet();

        for ( final NodePath path : this.nodePaths )
        {
            nodePathsAsStrings.add( path.toString() );
        }

        return nodePathsAsStrings;
    }
}
