package com.enonic.wem.core.elasticsearch.workspace;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.NodeVersionIds;
import com.enonic.wem.core.elasticsearch.ElasticsearchDao;
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
            repository( context.getRepository() ).
            document( storeWorkspaceDocument ).
            build().
            execute();
    }

    @Override
    public void delete( final EntityId entityId, final WorkspaceContext context )
    {
        DeleteNodeVersionCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repository( context.getRepository() ).
            workspace( context.getWorkspace() ).
            entityId( entityId ).
            build().
            execute();
    }

    @Override
    public NodeVersionId getCurrentVersion( final EntityId entityId, final WorkspaceContext context )
    {
        return GetNodeVersionIdByIdCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            workspace( context.getWorkspace() ).
            repository( context.getRepository() ).
            entityId( entityId ).
            build().
            execute();
    }

    @Override
    public NodeVersionIds getByVersionIds( final EntityIds entityIds, final WorkspaceContext context )
    {
        return GetNodeVersionIdsByIdsCommand.create().
            entityIds( entityIds ).
            workspace( context.getWorkspace() ).
            elasticsearchDao( this.elasticsearchDao ).
            repository( context.getRepository() ).
            build().
            execute();
    }

    @Override
    public NodeVersionId getByPath( final NodePath nodePath, final WorkspaceContext context )
    {
        return GetNodeVersionIdByPathCommand.create().
            repository( context.getRepository() ).
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
            repository( context.getRepository() ).
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
            repository( context.getRepository() ).
            parentPath( parentPath ).
            build().
            execute();
    }

    @Override
    public EntityIds findNodesWithDifferences( final CompareWorkspacesQuery query, final WorkspaceContext context )
    {
        return FindNodesWithDifferencesCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repository( context.getRepository() ).
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
            repository( context.getRepository() ).
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

