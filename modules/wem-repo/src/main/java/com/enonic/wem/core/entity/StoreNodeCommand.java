package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.version.NodeVersionDocument;
import com.enonic.wem.core.workspace.StoreWorkspaceDocument;
import com.enonic.wem.core.workspace.WorkspaceContext;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeVersionId;

public class StoreNodeCommand
    extends AbstractNodeCommand
{
    private Node node;

    private StoreNodeCommand( final Builder builder )
    {
        super( builder );
        node = builder.node;
    }

    public void execute()
    {
        final NodeVersionId updatedNodeVersionId = nodeDao.store( node );

        final Context context = ContextAccessor.current();

        this.versionService.store( NodeVersionDocument.create().
            nodeId( node.id() ).
            nodeVersionId( updatedNodeVersionId ).
            build(), context.getRepositoryId() );

        this.workspaceService.store( StoreWorkspaceDocument.create().
            id( node.id() ).
            nodeVersionId( updatedNodeVersionId ).
            build(), WorkspaceContext.from( context ) );

        this.indexService.store( node, updatedNodeVersionId, IndexContext.from( context ) );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Node node;

        private Builder()
        {
        }

        public Builder node( Node node )
        {
            this.node = node;
            return this;
        }

        public StoreNodeCommand build()
        {
            return new StoreNodeCommand( this );
        }
    }
}
