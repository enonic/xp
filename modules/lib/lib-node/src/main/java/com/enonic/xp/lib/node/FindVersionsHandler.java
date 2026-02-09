package com.enonic.xp.lib.node;

import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.GetNodeVersionsResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersions;

public class FindVersionsHandler
    extends AbstractNodeHandler
{
    private final NodeKey key;

    private final String cursor;

    private final Integer size;

    private FindVersionsHandler( final Builder builder )
    {
        super( builder );

        key = builder.key;
        cursor = builder.cursor;
        size = builder.size;
    }

    @Override
    public Object execute()
    {
        final GetNodeVersionsResult result;

        NodeId nodeId = getNodeId( key );
        if ( nodeId == null )
        {
            result = GetNodeVersionsResult.create().entityVersions( NodeVersions.empty() ).totalHits( 0 ).build();
        }
        else
        {
            GetNodeVersionsParams.Builder params = GetNodeVersionsParams.create().nodeId( nodeId ).cursor( cursor );

            if ( size != null )
            {
                params.size( size );
            }

            result = nodeService.getVersions( params.build() );
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

        private String cursor;

        private Integer size;

        private Builder()
        {
        }

        public Builder key( final NodeKey val )
        {
            key = val;
            return this;
        }

        public Builder cursor( final String cursor )
        {
            this.cursor = cursor;
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
