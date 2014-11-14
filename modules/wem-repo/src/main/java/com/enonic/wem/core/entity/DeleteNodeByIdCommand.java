package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.workspace.WorkspaceContext;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodeId;
import com.enonic.wem.repo.NodeVersionId;

public final class DeleteNodeByIdCommand
    extends AbstractDeleteNodeCommand
{
    private final NodeId nodeId;

    private DeleteNodeByIdCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
    }

    public Node execute()
    {
        final Context context = ContextAccessor.current();

        final NodeVersionId currentVersion = this.queryService.get( this.nodeId, IndexContext.from( context ) );

        final Node nodeToDelete = this.nodeDao.getByVersionId( currentVersion );

        doDeleteChildren( nodeToDelete );

        workspaceService.delete( nodeId, WorkspaceContext.from( context ) );

        indexService.delete( nodeId, IndexContext.from( context ) );

        return nodeToDelete;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractDeleteNodeCommand.Builder<Builder>
    {

        private NodeId nodeId;

        Builder()
        {
            super();
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public DeleteNodeByIdCommand build()
        {
            return new DeleteNodeByIdCommand( this );
        }
    }

}
