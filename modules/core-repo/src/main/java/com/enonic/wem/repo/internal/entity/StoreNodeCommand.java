package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.version.NodeVersionDocument;
import com.enonic.wem.repo.internal.workspace.StoreWorkspaceDocument;
import com.enonic.wem.repo.internal.workspace.WorkspaceContext;

public class StoreNodeCommand
    extends AbstractNodeCommand
{
    private final Node node;

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
            nodePath( node.path() ).
            build(), context.getRepositoryId() );

        this.workspaceService.store( StoreWorkspaceDocument.create().
            node( node ).
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

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.node );
        }

        public StoreNodeCommand build()
        {
            this.validate();
            return new StoreNodeCommand( this );
        }
    }
}
