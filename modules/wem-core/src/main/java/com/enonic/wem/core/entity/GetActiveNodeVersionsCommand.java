package com.enonic.wem.core.entity;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.GetActiveNodeVersionsResult;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;
import com.enonic.wem.core.version.VersionService;

public class GetActiveNodeVersionsCommand
    extends AbstractNodeCommand
{
    private final ImmutableSet<Workspace> workspaces;

    private final EntityId entityId;

    private final VersionService versionService;

    private GetActiveNodeVersionsCommand( final Builder builder )
    {
        super( builder );
        this.workspaces = ImmutableSet.copyOf( builder.workspaces );
        this.versionService = builder.versionService;
        this.entityId = builder.entityId;
    }

    public static Builder create( final Context context )
    {
        return new Builder( context );
    }

    public GetActiveNodeVersionsResult execute()
    {
        final GetActiveNodeVersionsResult.Builder builder = GetActiveNodeVersionsResult.create();

        for ( final Workspace workspace : workspaces )
        {
            try
            {
                final BlobKey blobKey = nodeDao.getBlobKey( entityId, workspace );
                builder.add( workspace, this.versionService.getVersion( blobKey ) );
            }
            catch ( NodeNotFoundException e )
            {
                // Don't add entry for this since it does not exist in that workspace
            }
        }

        return builder.build();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Set<Workspace> workspaces;

        private EntityId entityId;

        private VersionService versionService;

        public Builder( final Context context )
        {
            super( context );
        }

        public Builder workspaces( Set<Workspace> workspaces )
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

        private void validate()
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
