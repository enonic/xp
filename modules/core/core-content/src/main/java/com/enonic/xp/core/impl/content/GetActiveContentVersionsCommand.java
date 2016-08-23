package com.enonic.xp.core.impl.content;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.branch.BranchIds;
import com.enonic.xp.content.ActiveContentVersionEntry;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.GetActiveContentVersionsResult;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.RefreshMode;

public class GetActiveContentVersionsCommand
    extends AbstractContentCommand
{
    private final BranchIds branchIds;

    private final ContentId contentId;

    private GetActiveContentVersionsCommand( final Builder builder )
    {
        super( builder );
        branchIds = builder.branchIds;
        contentId = builder.contentId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public GetActiveContentVersionsResult execute()
    {
        this.nodeService.refresh( RefreshMode.STORAGE );

        final NodeId nodeId = NodeId.from( contentId.toString() );

        final GetActiveNodeVersionsResult activeNodeVersions = this.nodeService.getActiveVersions( GetActiveNodeVersionsParams.create().
            nodeId( nodeId ).
            branches( this.branchIds ).
            build() );

        final ContentVersionFactory contentVersionFactory = new ContentVersionFactory( this.translator, this.nodeService );

        final GetActiveContentVersionsResult.Builder builder = GetActiveContentVersionsResult.create();

        final ImmutableMap<BranchId, NodeVersionMetadata> nodeVersionsMap = activeNodeVersions.getNodeVersions();
        for ( final BranchId branchId : nodeVersionsMap.keySet() )
        {
            final NodeVersionMetadata nodeVersionMetadata = nodeVersionsMap.get( branchId );
            builder.add( ActiveContentVersionEntry.from( branchId, contentVersionFactory.create( nodeVersionMetadata ) ) );
        }

        return builder.build();
    }

    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private BranchIds branchIds;

        private ContentId contentId;

        private Builder()
        {
        }

        public Builder branches( final BranchIds branchIds )
        {
            this.branchIds = branchIds;
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
