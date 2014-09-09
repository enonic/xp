package com.enonic.wem.core.workspace.query;

import com.enonic.wem.api.entity.NodePath;

public class WorkspaceParentQuery
    extends AbstractWorkspaceQuery
{
    private final NodePath parentPath;

    private WorkspaceParentQuery( Builder builder )
    {
        super( builder );
        parentPath = builder.parentPath;
    }

    public static Builder create()
    {
        return new Builder();
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

    public static final class Builder
        extends AbstractWorkspaceQuery.Builder<Builder>
    {
        private NodePath parentPath;

        private Builder()
        {
        }

        public Builder parentPath( NodePath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        public WorkspaceParentQuery build()
        {
            return new WorkspaceParentQuery( this );
        }
    }
}
