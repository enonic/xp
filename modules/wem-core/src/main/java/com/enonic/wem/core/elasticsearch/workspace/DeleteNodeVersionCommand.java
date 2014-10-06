package com.enonic.wem.core.elasticsearch.workspace;

import org.elasticsearch.action.delete.DeleteRequest;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.entity.EntityId;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.repository.StorageNameResolver;
import com.enonic.wem.core.workspace.WorkspaceDocumentId;

public class DeleteNodeVersionCommand
    extends AbstractWorkspaceCommand
{
    private final Workspace workspace;

    private final EntityId entityId;

    private DeleteNodeVersionCommand( Builder builder )
    {
        super( builder );
        workspace = builder.workspace;
        entityId = builder.entityId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    void execute()
    {
        DeleteRequest deleteRequest = new DeleteRequest().
            index( StorageNameResolver.resolveStorageIndexName( this.repositoryId ) ).
            type( IndexType.WORKSPACE.getName() ).
            id( new WorkspaceDocumentId( this.entityId, this.workspace ).toString() ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.delete( deleteRequest );
    }


    static final class Builder
        extends AbstractWorkspaceCommand.Builder<Builder>
    {
        private Workspace workspace;

        private EntityId entityId;

        private Builder()
        {
        }

        public Builder workspace( Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        public Builder entityId( EntityId entityId )
        {
            this.entityId = entityId;
            return this;
        }

        public DeleteNodeVersionCommand build()
        {
            return new DeleteNodeVersionCommand( this );
        }
    }
}
