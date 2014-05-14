package com.enonic.wem.core.workspace.query;

import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Workspace;

public class WorkspacePathQuery
    extends AbstractWorkspaceQuery
{
    private NodePath nodePath;

    public WorkspacePathQuery( final Workspace workspace, final NodePath nodePath )
    {
        super( workspace );
        this.nodePath = nodePath;
    }

    public String getNodePathAsString()
    {
        return nodePath.toString();
    }
}
