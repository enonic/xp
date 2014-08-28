package com.enonic.wem.core.content;

import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.GetActiveContentVersionsResult;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.GetActiveNodeVersionsParams;
import com.enonic.wem.api.entity.GetActiveNodeVersionsResult;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeVersion;
import com.enonic.wem.api.entity.Workspace;

public class GetActiveContentVersionsCommand
    extends AbstractContentCommand
{

    private final ImmutableSet<Workspace> workspaces;

    private final ContentId contentId;

    private GetActiveContentVersionsCommand( final Builder builder )
    {
        super( builder );
        workspaces = ImmutableSet.copyOf( builder.workspaces );
        contentId = builder.contentId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public GetActiveContentVersionsResult execute()
    {
        final EntityId entityId = EntityId.from( contentId.toString() );

        final GetActiveNodeVersionsResult activeNodeVersions = this.nodeService.getActiveVersions( GetActiveNodeVersionsParams.create().
            entityId( entityId ).
            workspaces( this.workspaces ).
            build(), this.context );

        final ContentVersionsFactory contentVersionsFactory = new ContentVersionsFactory( this.translator );

        final GetActiveContentVersionsResult.Builder builder = GetActiveContentVersionsResult.create();

        final ImmutableMap<Workspace, NodeVersion> nodeVersionsMap = activeNodeVersions.getNodeVersions();
        for ( final Workspace workspace : nodeVersionsMap.keySet() )
        {
            final NodeVersion nodeVersion = nodeVersionsMap.get( workspace );

            final Node node = nodeService.getByBlobKey( nodeVersion.getBlobKey(), this.context );

            builder.add( workspace, contentVersionsFactory.create( node ) );
        }

        return builder.build();
    }

    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private Set<Workspace> workspaces = Sets.newHashSet();

        private ContentId contentId;

        private Builder()
        {
        }

        public Builder workspaces( final Set<Workspace> workspaces )
        {
            this.workspaces = workspaces;
            return this;
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public GetActiveContentVersionsCommand build()
        {
            return new GetActiveContentVersionsCommand( this );
        }
    }
}
