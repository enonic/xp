package com.enonic.wem.core.elasticsearch.workspace;

import javax.inject.Inject;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.NodeVersionIds;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.elasticsearch.ElasticsearchDao;
import com.enonic.wem.core.workspace.StoreWorkspaceDocument;
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.core.workspace.compare.query.CompareWorkspacesQuery;

public class ElasticsearchWorkspaceService
    implements WorkspaceService
{
    private ElasticsearchDao elasticsearchDao;

    @Override
    public void store( final StoreWorkspaceDocument storeWorkspaceDocument, final Context context )
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
    public void delete( final EntityId entityId, final Context context )
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
    public NodeVersionId getCurrentVersion( final EntityId entityId, final Context context )
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
    public NodeVersionId getWorkspaceVersion( final EntityId entityId, final Workspace workspace, final Context context )
    {
        return GetNodeVersionIdByIdCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            workspace( workspace ).
            repository( context.getRepository() ).
            entityId( entityId ).
            build().
            execute();
    }

    @Override
    public NodeVersionIds getByVersionIds( final EntityIds entityIds, final Context context )
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
    public NodeVersionId getByPath( final NodePath nodePath, final Context context )
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
    public NodeVersionIds getByPaths( final NodePaths nodePaths, final Context context )
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
    public NodeVersionIds findByParent( final NodePath parentPath, final Context context )
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
    public EntityIds findNodesWithDifferences( final CompareWorkspacesQuery query, final Context context )
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
    public boolean hasChildren( final NodePath parent, final Context context )
    {
        return GetHasChildrenCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repository( context.getRepository() ).
            workspace( context.getWorkspace() ).
            parentPath( parent ).
            build().
            execute();
    }

    @Inject
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}

