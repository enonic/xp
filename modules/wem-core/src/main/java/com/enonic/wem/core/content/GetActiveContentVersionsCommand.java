package com.enonic.wem.core.content;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.content.ActiveContentVersionEntry;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.GetActiveContentVersionsResult;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.api.workspace.Workspaces;
import com.enonic.wem.repo.GetActiveNodeVersionsParams;
import com.enonic.wem.repo.GetActiveNodeVersionsResult;
import com.enonic.wem.repo.NodeId;
import com.enonic.wem.repo.NodeVersion;

public class GetActiveContentVersionsCommand
    extends AbstractContentCommand
{
    private final Workspaces workspaces;

    private final ContentId contentId;

    private GetActiveContentVersionsCommand( final Builder builder )
    {
        super( builder );
        workspaces = builder.workspaces;
        contentId = builder.contentId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public GetActiveContentVersionsResult execute()
    {
        final NodeId nodeId = NodeId.from( contentId.toString() );

        final GetActiveNodeVersionsResult activeNodeVersions = this.nodeService.getActiveVersions( GetActiveNodeVersionsParams.create().
            nodeId( nodeId ).
            workspaces( this.workspaces ).
            build() );

        final ContentVersionFactory contentVersionFactory = new ContentVersionFactory( this.translator, this.nodeService );

        final GetActiveContentVersionsResult.Builder builder = GetActiveContentVersionsResult.create();

        final ImmutableMap<Workspace, NodeVersion> nodeVersionsMap = activeNodeVersions.getNodeVersions();
        for ( final Workspace workspace : nodeVersionsMap.keySet() )
        {
            final NodeVersion nodeVersion = nodeVersionsMap.get( workspace );
            builder.add( ActiveContentVersionEntry.from( workspace, contentVersionFactory.create( nodeVersion ) ) );
        }

        return builder.build();
    }

    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private Workspaces workspaces;

        private ContentId contentId;

        private Builder()
        {
        }

        public Builder workspaces( final Workspaces workspaces )
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
