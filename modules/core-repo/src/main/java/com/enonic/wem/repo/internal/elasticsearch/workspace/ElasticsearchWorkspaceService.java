package com.enonic.wem.repo.internal.elasticsearch.workspace;


import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.elasticsearch.GetQuery;
import com.enonic.wem.repo.internal.elasticsearch.ReturnFields;
import com.enonic.wem.repo.internal.index.IndexType;
import com.enonic.wem.repo.internal.index.query.NodeWorkspaceVersion;
import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.repository.StorageNameResolver;
import com.enonic.wem.repo.internal.workspace.NodeWorkspaceState;
import com.enonic.wem.repo.internal.workspace.StoreWorkspaceDocument;
import com.enonic.wem.repo.internal.workspace.WorkspaceContext;
import com.enonic.wem.repo.internal.workspace.WorkspaceDocumentId;
import com.enonic.wem.repo.internal.workspace.WorkspaceService;

public class ElasticsearchWorkspaceService
    implements WorkspaceService
{
    private ElasticsearchDao elasticsearchDao;

    @Override
    public void store( final StoreWorkspaceDocument storeWorkspaceDocument, final WorkspaceContext context )
    {
        StoreWorkspaceDocumentCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            workspace( context.getWorkspace() ).
            repository( context.getRepositoryId() ).
            document( storeWorkspaceDocument ).
            build().
            execute();
    }

    @Override
    public void delete( final NodeId nodeId, final WorkspaceContext context )
    {
        DeleteNodeVersionCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repository( context.getRepositoryId() ).
            workspace( context.getWorkspace() ).
            nodeId( nodeId ).
            build().
            execute();
    }

    @Override
    public NodeWorkspaceVersion get( final NodeId nodeId, final WorkspaceContext context )
    {
        final WorkspaceDocumentId workspaceDocumentId = new WorkspaceDocumentId( nodeId, context.getWorkspace() );

        final GetResult getResult = this.elasticsearchDao.get( GetQuery.create().
            id( workspaceDocumentId.toString() ).
            indexName( StorageNameResolver.resolveStorageIndexName( ContextAccessor.current().getRepositoryId() ) ).
            indexTypeName( IndexType.WORKSPACE.getName() ).
            returnFields( ReturnFields.from( WorkspaceIndexPath.VERSION_ID, WorkspaceIndexPath.STATE, WorkspaceIndexPath.PATH ) ).
            routing( nodeId.toString() ).
            build() );

        if ( getResult.isEmpty() )
        {
            return null;
        }

        final String state = getResult.getStringValue( WorkspaceIndexPath.STATE, true );
        final String nodeVersionId = getResult.getStringValue( WorkspaceIndexPath.VERSION_ID, true );
        final String path = getResult.getStringValue( WorkspaceIndexPath.PATH, true );

        return NodeWorkspaceVersion.create().
            nodePath( NodePath.newPath( path ).build() ).
            nodeVersionId( NodeVersionId.from( nodeVersionId ) ).
            state( NodeWorkspaceState.from( state ) ).
            build();
    }

    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}

