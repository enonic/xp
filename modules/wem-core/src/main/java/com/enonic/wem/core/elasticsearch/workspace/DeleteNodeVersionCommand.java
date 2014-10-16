package com.enonic.wem.core.elasticsearch.workspace;

import org.elasticsearch.action.delete.DeleteRequest;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.repository.StorageNameResolver;
import com.enonic.wem.core.workspace.WorkspaceDocumentId;

public class DeleteNodeVersionCommand
    extends AbstractWorkspaceCommand
{
    private final Workspace workspace;

    private final NodeId nodeId;

    private DeleteNodeVersionCommand( Builder builder )
    {
        super( builder );
        workspace = builder.workspace;
        nodeId = builder.nodeId;
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
            id( new WorkspaceDocumentId( this.nodeId, this.workspace ).toString() ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.delete( deleteRequest );
    }


    static final class Builder
        extends AbstractWorkspaceCommand.Builder<Builder>
    {
        private Workspace workspace;

        private NodeId nodeId;

        private Builder()
        {
        }

        public Builder workspace( Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        public Builder nodeId( NodeId nodeId )
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
