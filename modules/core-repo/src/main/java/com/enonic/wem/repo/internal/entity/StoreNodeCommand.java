package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.xp.core.context.Context;
import com.enonic.xp.core.context.ContextAccessor;
import com.enonic.xp.core.node.Node;
import com.enonic.xp.core.node.NodeVersionId;
import com.enonic.wem.repo.internal.branch.BranchContext;
import com.enonic.wem.repo.internal.branch.StoreBranchDocument;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.version.NodeVersionDocument;

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

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public Node execute()
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

        this.branchService.store( StoreBranchDocument.create().
            node( node ).
            nodeVersionId( nodeVersionId ).
            build(), BranchContext.from( context ) );

        this.indexServiceInternal.store( node, nodeVersionId, IndexContext.from( context ) );

        return this.nodeDao.getByVersionId( nodeVersionId );
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
