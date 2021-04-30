package com.enonic.xp.lib.node;

import com.enonic.xp.lib.node.mapper.NodeCommitEntryMapper;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;

public class GetCommitHandler
    extends AbstractNodeHandler
{
    private final NodeCommitId id;

    private GetCommitHandler( final Builder builder )
    {
        super( builder );
        id = builder.id;
    }

    @Override
    public Object execute()
    {
        final NodeCommitEntry entry = nodeService.getCommit( id );
        if ( entry == null )
        {
            return null;
        }

        return new NodeCommitEntryMapper( entry );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeCommitId id;

        private Builder()
        {
        }

        public Builder id( final NodeCommitId id )
        {
            this.id = id;
            return this;
        }

        public GetCommitHandler build()
        {
            return new GetCommitHandler( this );
        }
    }
}
