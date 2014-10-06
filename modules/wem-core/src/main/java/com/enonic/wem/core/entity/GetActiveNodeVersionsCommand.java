package com.enonic.wem.core.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.api.workspace.Workspaces;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceContext;

public class GetActiveNodeVersionsCommand
    extends AbstractNodeCommand
{
    private final Workspaces workspaces;

    private final EntityId entityId;

    private final VersionService versionService;

    private GetActiveNodeVersionsCommand( final Builder builder )
    {
        super( builder );
        this.workspaces = builder.workspaces;
        this.versionService = builder.versionService;
        this.entityId = builder.entityId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public GetActiveNodeVersionsResult execute()
    {
        final GetActiveNodeVersionsResult.Builder builder = GetActiveNodeVersionsResult.create();

        for ( final Workspace workspace : workspaces )
        {
            final Context context = Context.current();

            final NodeVersionId currentVersion =
                this.workspaceService.getCurrentVersion( this.entityId, WorkspaceContext.from( workspace, context.getRepositoryId() ) );

            if ( currentVersion != null )
            {
                builder.add( workspace, this.versionService.getVersion( currentVersion, context.getRepositoryId() ) );
            }
        }
        return builder.build();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Workspaces workspaces;

        private EntityId entityId;

        private VersionService versionService;

        public Builder()
        {
            super();
        }

        public Builder workspaces( final Workspaces workspaces )
        {
            this.workspaces = workspaces;
            return this;
        }

        public Builder entityId( EntityId entityId )
        {
            this.entityId = entityId;
            return this;
        }

        public Builder versionService( final VersionService versionService )
        {
            this.versionService = versionService;
            return this;
        }

        protected void validate()
        {
            Preconditions.checkNotNull( this.entityId );
            Preconditions.checkNotNull( this.workspaces );
            Preconditions.checkNotNull( this.versionService );
            Preconditions.checkNotNull( this.nodeDao );
        }

        public GetActiveNodeVersionsCommand build()
        {
            this.validate();
            return new GetActiveNodeVersionsCommand( this );
        }
    }
}
