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

    private final NodeId id;

    private PushNodeCommand( final Builder builder )
    {
        super( builder );
        this.target = builder.target;
        this.id = builder.id;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public Node execute()
    {
        final Context context = Context.current();

        final NodeVersionId currentVersion = this.queryService.get( id, IndexContext.from( context ) );

        final Node currentNode = nodeDao.getByVersionId( currentVersion );

        this.workspaceService.store( StoreWorkspaceDocument.create().
            nodeVersionId( currentVersion ).
            id( this.id ).
            path( currentNode.path() ).
            parentPath( currentNode.parent() ).
            build(), WorkspaceContext.from( this.target, context.getRepositoryId() ) );

        this.indexService.store( currentNode, currentVersion, IndexContext.from( this.target, context.getRepositoryId() ) );

        return NodeHasChildResolver.create().
            workspaceService( this.workspaceService ).
            build().
            resolve( currentNode );
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Workspace target;

        private NodeId id;

        Builder()
        {
            super();
        }

        public Builder target( final Workspace target )
        {
            this.target = target;
            return this;
        }

        public Builder id( final NodeId nodeId )
        {
            this.id = nodeId;
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
