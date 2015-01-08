package com.enonic.wem.repo.internal.elasticsearch.workspace;

import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.WorkspaceDiffResult;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.workspace.StoreWorkspaceDocument;
import com.enonic.wem.repo.internal.workspace.WorkspaceContext;
import com.enonic.wem.repo.internal.workspace.WorkspaceService;
import com.enonic.wem.repo.internal.workspace.compare.query.WorkspaceDiffQuery;

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
    public WorkspaceDiffResult diff( final WorkspaceDiffQuery query, final WorkspaceContext context )
    {
        return WorkspaceDiffCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            query( query ).
            repositoryId( context.getRepositoryId() ).
            build().
            execute();
    }

    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}

