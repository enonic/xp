package com.enonic.xp.lib.node;

import java.util.Objects;
import java.util.stream.Collectors;

import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeVersionId;

public final class GetNodeHandler
    extends AbstractNodeHandler
{
    private NodeKeys keys;

    private GetNodeHandler( final Builder builder )
    {
        super( builder );
        keys = builder.keys;
    }

    @Override
    public Object execute()
    {
        if ( keys.singleValue() )
        {
            return getByKey( keys.first() );
        }
        else
        {
            return this.keys.stream().
                map( this::getByKey ).
                filter( Objects::nonNull ).
                collect( Collectors.toList() );
        }
    }

    private NodeMapper getByKey( final NodeKey key )
    {
        if ( key.isId() )
        {
            return key.getVersionId() == null
                ? getById( key.getAsNodeId() )
                : getByIdAndVersionId( key.getAsNodeId(), NodeVersionId.from( key.getVersionId() ) );
        }
        else
        {
            throw new UnsupportedOperationException("Key must be id: " + key);
        }
    }

    private NodeMapper getById( final NodeId key )
    {
        try
        {
            return convert( this.nodeService.getById( key ) );
        }
        catch ( final NodeNotFoundException e )
        {
            return null;
        }
    }

    private NodeMapper getByIdAndVersionId( final NodeId id, final NodeVersionId versionId )
    {
        try
        {
            return convert( this.nodeService.getByIdAndVersionId( id, versionId ) );
        }
        catch ( final NodeNotFoundException e )
        {
            return null;
        }
    }

    private NodeMapper convert( final Node content )
    {
        return content == null ? null : new NodeMapper( content );
    }

    public void setKeys( final String[] keys )
    {
        this.keys = NodeKeys.from( keys );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey key;

        private NodeKeys keys;

        private Builder()
        {
        }

        public Builder key( final NodeKey val )
        {
            key = val;
            return this;
        }

        public Builder keys( final NodeKeys val )
        {
            keys = val;
            return this;
        }

        public GetNodeHandler build()
        {
            return new GetNodeHandler( this );
        }
    }
}
