package com.enonic.wem.core.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.version.NodeVersionDocument;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.StoreWorkspaceDocument;
import com.enonic.wem.core.workspace.WorkspaceContext;
import com.enonic.wem.core.workspace.WorkspaceService;

abstract class AbstractNodeCommand
{
    final IndexService indexService;

    final NodeDao nodeDao;

    final WorkspaceService workspaceService;

    final VersionService versionService;

    AbstractNodeCommand( final Builder builder )
    {
        this.indexService = builder.indexService;
        this.nodeDao = builder.nodeDao;
        this.workspaceService = builder.workspaceService;
        this.versionService = builder.versionService;
    }

    protected Node doGetNode( final NodeId id, final boolean resolveHasChild )
    {
        final Context context = Context.current();

        final NodeVersionId currentVersion = this.workspaceService.getCurrentVersion( id, WorkspaceContext.from( context ) );

        if ( currentVersion == null )
        {
            throw new NodeNotFoundException( "Node with id " + id + " not found in workspace " + context.getWorkspace().getName() );
        }

        final Node node = nodeDao.getByVersionId( currentVersion );

        return !resolveHasChild ? node : NodeHasChildResolver.create().
            workspaceService( this.workspaceService ).
            build().
            resolve( node );

    }

    protected void doStoreNode( final Node updatedNode )
    {
        final NodeVersionId updatedNodeVersionId = nodeDao.store( updatedNode );

        final Context context = Context.current();

        this.versionService.store( NodeVersionDocument.create().
            nodeId( updatedNode.id() ).
            nodeVersionId( updatedNodeVersionId ).
            build(), context.getRepositoryId() );

        this.workspaceService.store( StoreWorkspaceDocument.create().
            path( updatedNode.path() ).
            parentPath( updatedNode.parent() ).
            id( updatedNode.id() ).
            nodeVersionId( updatedNodeVersionId ).
            build(), WorkspaceContext.from( context ) );

        this.indexService.store( updatedNode, IndexContext.from( context ) );
    }

    public static class Builder<B extends Builder>
    {
        IndexService indexService;

        NodeDao nodeDao;

        WorkspaceService workspaceService;

        VersionService versionService;

        Builder()
        {
        }

        @SuppressWarnings("unchecked")
        public B indexService( final IndexService indexService )
        {
            this.indexService = indexService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B workspaceService( final WorkspaceService workspaceService )
        {
            this.workspaceService = workspaceService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B versionService( final VersionService versionService )
        {
            this.versionService = versionService;
            return (B) this;
        }


        @SuppressWarnings("unchecked")
        public B nodeDao( final NodeDao nodeDao )
        {
            this.nodeDao = nodeDao;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( indexService );
            Preconditions.checkNotNull( versionService );
            Preconditions.checkNotNull( nodeDao );
            Preconditions.checkNotNull( workspaceService );
        }


    }

}
