package com.enonic.xp.lib.node;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
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
        final ImmutableList.Builder<String> deletedNodeIds = ImmutableList.builder();

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

        return deletedNodeIds.build();
    }

    private NodeIds deleteByKey( final NodeKey key )
    {
        if ( key.isId() )
        {
            return deleteById( key.getAsNodeId() );

        }
        else
        {
            return deleteByPath( key.getAsPath() );
        }
    }

    private NodeIds deleteByPath( final NodePath key )
    {
        try
        {
            return this.nodeService.deleteByPath( key );
        }
        catch ( final NodeNotFoundException e )
        {
            return NodeIds.empty();
        }
    }

    private NodeIds deleteById( final NodeId key )
    {
        try
        {
            return this.nodeService.deleteById( key );
        }
        catch ( final NodeNotFoundException e )
        {
            return NodeIds.empty();
        }
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
