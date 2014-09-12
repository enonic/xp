package com.enonic.wem.core.workspace.query;

import com.enonic.wem.api.entity.NodePath;

public class WorkspaceHasChildrenQuery
    extends AbstractWorkspaceQuery
{
    private final NodePath parent;

    private WorkspaceHasChildrenQuery( final Builder builder )
    {
        super( builder );
        parent = builder.parent;
    }

    public NodePath getParent()
    {
        return parent;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractWorkspaceQuery.Builder<Builder>
    {
        private NodePath parent;

        private Builder()
        {
        }

        public Builder parent( NodePath parent )
        {
            this.parent = parent;
            return this;
        }

        public WorkspaceHasChildrenQuery build()
        {
            return new WorkspaceHasChildrenQuery( this );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof WorkspaceHasChildrenQuery ) )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        final WorkspaceHasChildrenQuery that = (WorkspaceHasChildrenQuery) o;

        if ( parent != null ? !parent.equals( that.parent ) : that.parent != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + ( parent != null ? parent.hashCode() : 0 );
        return result;
    }
}
