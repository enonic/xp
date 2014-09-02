package com.enonic.wem.core.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;

public abstract class AbstractNodeCommand
{
    protected final Context context;

    protected final IndexService indexService;

    protected final NodeDao nodeDao;

    protected final WorkspaceService workspaceService;

    protected final VersionService versionService;

    public AbstractNodeCommand( final Builder builder )
    {
        this.context = builder.context;
        this.indexService = builder.indexService;
        this.nodeDao = builder.nodeDao;
        this.workspaceService = builder.workspaceService;
        this.versionService = builder.versionService;
    }

    public Context getContext()
    {
        return context;
    }

    protected NodeVersionId getCurrentVersionInWorkspace( final Workspace workspace, final EntityId id, final boolean failOnNull )
    {
        final NodeVersionId currentVersion = workspaceService.getCurrentVersion( new WorkspaceIdQuery( workspace, id ) );

        if ( currentVersion == null && failOnNull )
        {
            throw new NodeNotFoundException( "Node with id " + id + " not found in workspace " + workspace );
        }
        return currentVersion;
    }

    protected Node getCurrentNodeInWorkspace( final Workspace workspace, final EntityId id, final boolean failOnNull )
    {
        final NodeVersionId currentVersion = workspaceService.getCurrentVersion( new WorkspaceIdQuery( workspace, id ) );

        if ( currentVersion == null && failOnNull )
        {
            throw new NodeNotFoundException( "Node with id " + id + " not found in workspace " + workspace );
        }

        return nodeDao.getByVersionId( currentVersion );
    }

    public static class Builder<B extends Builder>
    {
        protected Context context;

        protected IndexService indexService;

        protected NodeDao nodeDao;

        protected WorkspaceService workspaceService;

        protected VersionService versionService;

        protected Builder( final Context context )
        {
            this.context = context;
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

        protected void validate()
        {
            Preconditions.checkNotNull( indexService );
            Preconditions.checkNotNull( versionService );
            Preconditions.checkNotNull( nodeDao );
            Preconditions.checkNotNull( workspaceService );
        }


    }

}
