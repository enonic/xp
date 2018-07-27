package com.enonic.xp.lib.node;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.GetActiveNodeVersionsResult;

public class GetActiveVersionsHandler
    extends AbstractNodeHandler
{
    private final NodeKey nodeKey;

    private final Branches branches;

    private GetActiveVersionsHandler( final Builder builder )
    {
        super( builder );

        nodeKey = builder.nodeKey;
        branches = builder.branches;
    }

    @Override
    public Object execute()
    {
        final GetActiveNodeVersionsResult result = nodeService.getActiveVersions( makeGetActiveNodeVersionsParams() );

        return new ActiveNodeVersionsResultMapper( result );
    }

    public static Builder create()
    {
        return new Builder();
    }

    private GetActiveNodeVersionsParams makeGetActiveNodeVersionsParams()
    {
        return GetActiveNodeVersionsParams.create().nodeId( getNodeId( nodeKey ) ).branches( branches ).build();
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey nodeKey;

        private Branches branches;

        private Builder()
        {
        }

        public Builder key( final NodeKey val )
        {
            nodeKey = val;
            return this;
        }

        public Builder branches( final Branches val )
        {
            branches = val;
            return this;
        }

        public GetActiveVersionsHandler build()
        {
            return new GetActiveVersionsHandler( this );
        }
    }
}
