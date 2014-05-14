package com.enonic.wem.core.workspace.query;

import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Workspace;

public class WorkspaceParentQuery
    extends AbstractWorkspaceQuery
{
    private final NodePath parentPath;

    public WorkspaceParentQuery( final Workspace workspace, final NodePath parentPath )
    {
        super( workspace );
        this.parentPath = parentPath;
    }

    public String getParentPath()
    {
        return parentPath.toString();
    }
}
