package com.enonic.wem.core.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceService;

public abstract class AbstractNodeCommand
{
    protected final Context context;

    protected final IndexService indexService;

    protected final NodeDao nodeDao;

    protected final WorkspaceService workspaceService;

    final VersionService versionService;

    public AbstractNodeCommand( final Builder builder )
    {
        this.context = builder.context;
        this.indexService = builder.indexService;
        this.nodeDao = builder.nodeDao;
        this.workspaceService = builder.workspaceService;
        this.versionService = builder.versionService;
    }

    public static class Builder<B extends Builder>
    {
        final Context context;

        IndexService indexService;

        protected NodeDao nodeDao;

        WorkspaceService workspaceService;

        VersionService versionService;

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
