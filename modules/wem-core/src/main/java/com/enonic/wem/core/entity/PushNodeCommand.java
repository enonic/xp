package com.enonic.wem.core.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.workspace.StoreWorkspaceDocument;
import com.enonic.wem.core.workspace.WorkspaceContext;

public class PushNodeCommand
    extends AbstractNodeCommand
{
    private final Workspace target;

    private final EntityId id;

    private PushNodeCommand( final Builder builder )
    {
        super( builder );
        this.target = builder.target;
        this.id = builder.id;
    }

    public static Builder create( final Context context )
    {
        return new Builder( context );
    }


    Node execute()
    {
        final NodeVersionId currentVersion = this.workspaceService.getCurrentVersion( id, WorkspaceContext.from( context ) );

        final Node currentNode = nodeDao.getByVersionId( currentVersion );

        this.workspaceService.store( StoreWorkspaceDocument.create().
            nodeVersionId( currentVersion ).
            id( this.id ).
            path( currentNode.path() ).
            parentPath( currentNode.parent() ).
            build(), WorkspaceContext.from( this.target, this.context.getRepository() ) );

        this.indexService.store( currentNode, IndexContext.from( this.target, this.context.getRepository() ) );

        return NodeHasChildResolver.create().
            workspaceService( this.workspaceService ).
            context( this.context ).
            build().
            resolve( currentNode );
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Workspace target;

        private EntityId id;

        Builder( final Context context )
        {
            super( context );
        }

        public Builder target( final Workspace target )
        {
            this.target = target;
            return this;
        }

        public Builder id( final EntityId entityId )
        {
            this.id = entityId;
            return this;
        }

        public PushNodeCommand build()
        {
            validate();
            return new PushNodeCommand( this );
        }

        protected void validate()
        {
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( id );
        }
    }


}
