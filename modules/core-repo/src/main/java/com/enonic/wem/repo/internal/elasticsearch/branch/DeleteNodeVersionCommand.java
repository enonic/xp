package com.enonic.wem.repo.internal.elasticsearch.branch;

import org.elasticsearch.action.delete.DeleteRequest;

import com.enonic.wem.api.branch.Branch;
import com.enonic.wem.api.index.IndexType;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.repo.internal.branch.BranchDocumentId;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;

public class DeleteNodeVersionCommand
    extends AbstractBranchCommand
{
    private final Branch branch;

    private final NodeId nodeId;

    private DeleteNodeVersionCommand( Builder builder )
    {
        super( builder );
        branch = builder.branch;
        nodeId = builder.nodeId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    void execute()
    {
        DeleteRequest deleteRequest = new DeleteRequest().
            index( IndexNameResolver.resolveStorageIndexName( this.repositoryId ) ).
            type( IndexType.BRANCH.getName() ).
            id( new BranchDocumentId( this.nodeId, this.branch ).toString() ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.delete( deleteRequest );
    }


    static final class Builder
        extends AbstractBranchCommand.Builder<Builder>
    {
        private Branch branch;

        private NodeId nodeId;

        private Builder()
        {
        }

        public Builder branch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public DeleteNodeVersionCommand build()
        {
            return new DeleteNodeVersionCommand( this );
        }
    }
}
