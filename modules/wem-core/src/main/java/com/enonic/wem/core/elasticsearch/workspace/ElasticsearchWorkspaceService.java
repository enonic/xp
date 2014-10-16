package com.enonic.wem.core.elasticsearch.workspace;

import com.enonic.wem.core.elasticsearch.ElasticsearchDao;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodeIds;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.NodePaths;
import com.enonic.wem.core.entity.NodeVersionId;
import com.enonic.wem.core.entity.NodeVersionIds;
import com.enonic.wem.core.workspace.StoreWorkspaceDocument;
import com.enonic.wem.core.workspace.WorkspaceContext;
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.core.workspace.compare.query.CompareWorkspacesQuery;

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
    public NodeVersionId getCurrentVersion( final NodeId nodeId, final WorkspaceContext context )
    {
        return GetNodeVersionIdByIdCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            workspace( context.getWorkspace() ).
            repository( context.getRepositoryId() ).
            nodeId( nodeId ).
            build().
            execute();
    }

    @Override
    public NodeVersionIds getByVersionIds( final NodeIds nodeIds, final WorkspaceContext context )
    {
        return GetNodeVersionIdsByIdsCommand.create().
            nodeIds( nodeIds ).
            workspace( context.getWorkspace() ).
            elasticsearchDao( this.elasticsearchDao ).
            repository( context.getRepositoryId() ).
            build().
            execute();
    }

    @Override
    public NodeVersionId getByPath( final NodePath nodePath, final WorkspaceContext context )
    {
        return GetNodeVersionIdByPathCommand.create().
            repository( context.getRepositoryId() ).
            elasticsearchDao( this.elasticsearchDao ).
            workspace( context.getWorkspace() ).
            nodePath( nodePath ).
            build().
            execute();
    }

    @Override
    public NodeVersionIds getByPaths( final NodePaths nodePaths, final WorkspaceContext context )
    {
        return GetNodeVersionIdsByPathsCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repository( context.getRepositoryId() ).
            workspace( context.getWorkspace() ).
            nodePaths( nodePaths ).
            build().
            execute();
    }

    @Override
    public NodeVersionIds findByParent( final NodePath parentPath, final WorkspaceContext context )
    {
        return FindNodeVersionIdsByParentCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            workspace( context.getWorkspace() ).
            repository( context.getRepositoryId() ).
            parentPath( parentPath ).
            build().
            execute();
    }

    @Override
    public NodeIds findNodesWithDifferences( final CompareWorkspacesQuery query, final WorkspaceContext context )
    {
        return FindNodesWithDifferencesCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repository( context.getRepositoryId() ).
            source( query.getSource() ).
            target( query.getTarget() ).
            build().
            execute();
    }

    @Override
    public boolean hasChildren( final NodePath parent, final WorkspaceContext context )
    {
        return GetHasChildrenCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repository( context.getRepositoryId() ).
            workspace( context.getWorkspace() ).
            parentPath( parent ).
            build().
            execute();
    }

    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}

