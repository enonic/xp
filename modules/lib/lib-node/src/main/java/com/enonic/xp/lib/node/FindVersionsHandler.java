package com.enonic.xp.lib.node;

import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionMetadatas;
import com.enonic.xp.node.NodeVersionQueryResult;

public class FindVersionsHandler
    extends AbstractNodeHandler
{
    private final NodeKey key;

    private final int from;

    private final int size;

    private FindVersionsHandler( final Builder builder )
    {
        super( builder );

        key = builder.key;
        from = builder.from == null ? 0 : builder.from;
        size = builder.size == null ? 10 : builder.size;
    }

    @Override
    public Object execute()
    {
        final NodeVersionQueryResult result;

        NodeId nodeId = getNodeId( key );
        if ( nodeId == null )
        {
            result = NodeVersionQueryResult.create().entityVersions( NodeVersionMetadatas.empty() ).build();
        }
        else
        {
            GetNodeVersionsParams params = GetNodeVersionsParams.create().
                nodeId( nodeId ).
                from( from ).
                size( size ).
                build();
            result = nodeService.findVersions( params );
        }

        return new NodeVersionsQueryResultMapper( result );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey key;

        private Integer from;

        private Integer size;

        private Builder()
        {
        }

        public Builder key( final NodeKey val )
        {
            key = val;
            return this;
        }

        public Builder from( final Integer from )
        {
            this.from = from;
            return this;
        }

        public Builder size( final Integer size )
        {
            this.size = size;
            return this;
        }

        public FindVersionsHandler build()
        {
            return new FindVersionsHandler( this );
        }
    }
}
