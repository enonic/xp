package com.enonic.xp.lib.node;

import java.util.ArrayList;
import java.util.Collection;

import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.support.AbstractImmutableEntitySet;

public final class DeleteNodeHandler
    extends AbstractNodeHandler
{
    private NodeKeys keys;

    private DeleteNodeHandler( final Builder builder )
    {
        super( builder );
        keys = builder.keys;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Collection<String> execute()
    {
        final ArrayList<String> deletedNodeIds = new ArrayList<>();

        if ( keys.singleValue() )
        {
            deleteByKey( keys.first() ).
                stream().
                map( NodeId::toString ).
                forEach( deletedNodeIds::add );
        }
        else
        {
            keys.stream().
                map( this::deleteByKey ).
                flatMap( AbstractImmutableEntitySet::stream ).
                map( NodeId::toString ).
                forEach( deletedNodeIds::add );
        }

        return deletedNodeIds;
    }

    private NodeIds deleteByKey( final NodeKey key )
    {
        final DeleteNodeParams.Builder params = DeleteNodeParams.create();

        if ( key.isId() )
        {
            params.nodeId( key.getAsNodeId() );

        }
        else
        {
            params.nodePath( key.getAsPath() );
        }
        return this.nodeService.delete( params.build() ).getNodeIds();
    }

    public void setKeys( final String[] keys )
    {
        this.keys = NodeKeys.from( keys );
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKeys keys;

        private Builder()
        {
        }

        public Builder keys( final NodeKeys val )
        {
            keys = val;
            return this;
        }

        public DeleteNodeHandler build()
        {
            return new DeleteNodeHandler( this );
        }
    }
}
