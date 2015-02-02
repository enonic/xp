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

    private final boolean updateMetadataOnly;

    private StoreNodeCommand( final Builder builder )
    {
        super( builder );
        this.node = builder.node;
        this.updateMetadataOnly = builder.updateMetadataOnly;
    }

    public void execute()
    {
        final Context context = ContextAccessor.current();

        final NodeVersionId nodeVersionId;

        if ( !updateMetadataOnly )
        {
            nodeVersionId = nodeDao.store( node );

            this.versionService.store( NodeVersionDocument.create().
                nodeId( node.id() ).
                nodeVersionId( nodeVersionId ).
                nodePath( node.path() ).
                build(), context.getRepositoryId() );
        }
        else
        {
            nodeVersionId = this.queryService.get( node.id(), IndexContext.from( context ) );
        }

        this.workspaceService.store( StoreWorkspaceDocument.create().
            node( node ).
            nodeVersionId( nodeVersionId ).
            build(), WorkspaceContext.from( context ) );

        this.indexService.store( node, nodeVersionId, IndexContext.from( context ) );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }


    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Node node;

        private boolean updateMetadataOnly = false;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder node( Node node )
        {
            this.node = node;
            return this;
        }

        public Builder updateMetadataOnly( final boolean updateMetadataOnly )
        {
            this.updateMetadataOnly = updateMetadataOnly;
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
