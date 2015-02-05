package com.enonic.wem.repo.internal.elasticsearch.workspace;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.repo.internal.elasticsearch.xcontent.WorkspaceXContentBuilderFactory;
import com.enonic.wem.repo.internal.index.IndexType;
import com.enonic.wem.repo.internal.repository.StorageNameResolver;
import com.enonic.wem.repo.internal.version.NodeVersionDocumentId;
import com.enonic.wem.repo.internal.workspace.StoreWorkspaceDocument;
import com.enonic.wem.repo.internal.workspace.WorkspaceDocumentId;

public class StoreWorkspaceDocumentCommand
    extends AbstractWorkspaceCommand
{
    private final StoreWorkspaceDocument document;

    private final Workspace workspace;

    private StoreWorkspaceDocumentCommand( Builder builder )
    {
        super( builder );
        document = builder.document;
        workspace = builder.workspace;
    }

    public static Builder create()
    {
        return new Builder();
    }

    void execute()
    {
        final WorkspaceDocumentId workspaceDocumentId = new WorkspaceDocumentId( document.getNode().id(), this.workspace );

        final IndexRequest publish = Requests.indexRequest().
            index( StorageNameResolver.resolveStorageIndexName( this.repositoryId ) ).
            type( IndexType.WORKSPACE.getName() ).
            source( WorkspaceXContentBuilderFactory.create( document, this.workspace ) ).
            id( workspaceDocumentId.toString() ).
            parent( new NodeVersionDocumentId( document.getNode().id(), document.getNodeVersionId() ).toString() ).
            routing( document.getNode().id().toString() ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.store( publish );
    }

    static final class Builder
        extends AbstractWorkspaceCommand.Builder<Builder>
    {
        private StoreWorkspaceDocument document;

        private Workspace workspace;

        private Builder()
        {
        }

        public Builder document( StoreWorkspaceDocument document )
        {
            this.document = document;
            return this;
        }

        public Builder workspace( Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        public StoreWorkspaceDocumentCommand build()
        {
            return new StoreWorkspaceDocumentCommand( this );
        }
    }
}
