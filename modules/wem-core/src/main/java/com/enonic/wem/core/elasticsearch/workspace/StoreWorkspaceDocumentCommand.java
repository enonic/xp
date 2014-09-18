package com.enonic.wem.core.elasticsearch.workspace;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.repository.StorageNameResolver;
import com.enonic.wem.core.workspace.StoreWorkspaceDocument;
import com.enonic.wem.core.workspace.WorkspaceDocumentId;

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
        final WorkspaceDocumentId workspaceDocumentId = new WorkspaceDocumentId( document.getEntityId(), this.workspace );

        final IndexRequest publish = Requests.indexRequest().
            index( StorageNameResolver.resolveStorageIndexName( this.repository ) ).
            type( IndexType.WORKSPACE.getName() ).
            source( WorkspaceXContentBuilderFactory.create( document, this.workspace ) ).
            id( workspaceDocumentId.toString() ).
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
