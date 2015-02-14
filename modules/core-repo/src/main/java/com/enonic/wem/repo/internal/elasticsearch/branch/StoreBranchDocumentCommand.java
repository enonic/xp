package com.enonic.wem.repo.internal.elasticsearch.branch;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import com.enonic.xp.core.branch.Branch;
import com.enonic.xp.core.index.IndexType;
import com.enonic.wem.repo.internal.branch.BranchDocumentId;
import com.enonic.wem.repo.internal.branch.StoreBranchDocument;
import com.enonic.wem.repo.internal.elasticsearch.xcontent.BranchXContentBuilderFactory;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.wem.repo.internal.version.NodeVersionDocumentId;

public class StoreBranchDocumentCommand
    extends AbstractBranchCommand
{
    private final StoreBranchDocument document;

    private final Branch branch;

    private StoreBranchDocumentCommand( Builder builder )
    {
        super( builder );
        document = builder.document;
        branch = builder.branch;
    }

    public static Builder create()
    {
        return new Builder();
    }

    void execute()
    {
        final BranchDocumentId branchDocumentId = new BranchDocumentId( document.getNode().id(), this.branch );

        final IndexRequest publish = Requests.indexRequest().
            index( IndexNameResolver.resolveStorageIndexName( repositoryId ) ).
            type( IndexType.BRANCH.getName() ).
            source( BranchXContentBuilderFactory.create( document, this.branch ) ).
            id( branchDocumentId.toString() ).
            parent( new NodeVersionDocumentId( document.getNode().id(), document.getNodeVersionId() ).toString() ).
            routing( document.getNode().id().toString() ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.store( publish );
    }

    static final class Builder
        extends AbstractBranchCommand.Builder<Builder>
    {
        private StoreBranchDocument document;

        private Branch branch;

        private Builder()
        {
        }

        public Builder document( StoreBranchDocument document )
        {
            this.document = document;
            return this;
        }

        public Builder branch( Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public StoreBranchDocumentCommand build()
        {
            return new StoreBranchDocumentCommand( this );
        }
    }
}
