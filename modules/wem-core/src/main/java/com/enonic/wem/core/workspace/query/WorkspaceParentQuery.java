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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof WorkspaceParentQuery ) )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        final WorkspaceParentQuery that = (WorkspaceParentQuery) o;

        if ( !parentPath.equals( that.parentPath ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + parentPath.hashCode();
        return result;
    }
}
