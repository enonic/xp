package com.enonic.xp.repo.impl.entity;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
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

    Node execute()
    {
        final Context context = ContextAccessor.current();

        final Node node = doGetByPath( this.nodePath, false );

        if ( node != null )
        {
            deleteNodeWithChildren( node, context );
        }

        return node;
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
        extends AbstractDeleteNodeCommand.Builder<Builder>
    {
        private NodePath nodePath;

        Builder()
        {
            super();
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
