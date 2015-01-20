package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeNotFoundException;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.repo.internal.index.IndexContext;

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

        final NodeVersionId version = this.queryService.get( this.nodePath, IndexContext.from( context ) );

        if ( version == null )
        {
            throw new NodeNotFoundException( "Node with path " + this.nodeDao + " not found in workspace " + context.getWorkspace() );
        }

        final Node nodeToDelete = nodeDao.getByVersionId( version );

        deleteNodeWithChildren( nodeToDelete, context );

        return nodeToDelete;
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
