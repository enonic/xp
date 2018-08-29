package com.enonic.xp.lib.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionMetadata;

public class GetActiveVersionHandler
    extends AbstractNodeHandler
{
    private final NodeKey key;

    private GetActiveVersionHandler( final Builder builder )
    {
        super( builder );
        key = builder.key;
    }

    @Override
    public Object execute()
    {
        final NodeId nodeId = getNodeId( key );
        if ( nodeId == null )
        {
            return null;
        }

        final Branch branch = ContextAccessor.current().getBranch();
        final GetActiveNodeVersionsParams params = GetActiveNodeVersionsParams.create().
            nodeId( nodeId ).
            branches( Branches.from( branch ) ).
            build();
        final NodeVersionMetadata nodeVersionMetadata = nodeService.getActiveVersions( params ).
            getNodeVersions().
            get( branch );
        if ( nodeVersionMetadata == null )
        {
            return null;
        }

        return new NodeVersionMapper( nodeVersionMetadata );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey key;

        private Builder()
        {
        }

        public Builder key( final NodeKey val )
        {
            key = val;
            return this;
        }

        public GetActiveVersionHandler build()
        {
            return new GetActiveVersionHandler( this );
        }
    }
}
