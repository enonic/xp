package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodePath;

final class DeleteNodeByPathCommand
    extends AbstractDeleteNodeCommand
{
    private final NodePath nodePath;

    private DeleteNodeByPathCommand( final Builder builder )
    {
        super( builder );
        this.nodePath = builder.nodePath;
    }

    NodeBranchEntries execute()
    {
        final Node node = doGetByPath( nodePath );

        return node != null ? deleteNodeWithChildren( node, null ) : NodeBranchEntries.empty();
    }

    static Builder create()
    {
        return new Builder();
    }

    static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    static class Builder
        extends AbstractDeleteNodeCommand.Builder<Builder>
    {
        private NodePath nodePath;

        Builder()
        {
            super();
        }

        Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.nodePath );
        }

        DeleteNodeByPathCommand build()
        {
            this.validate();
            return new DeleteNodeByPathCommand( this );
        }
    }

}
