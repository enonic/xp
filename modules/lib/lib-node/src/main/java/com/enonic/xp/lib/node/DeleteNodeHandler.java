package com.enonic.xp.lib.node;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;

public final class DeleteNodeHandler
    extends BaseNodeHandler
{
    private String key;

    private String[] keys;

    @Override
    protected Collection<String> doExecute()
    {
        final ImmutableList.Builder<String> deletedNodeIds = ImmutableList.builder();
        if ( key != null )
        {
            deleteByKey( key ).
                stream().
                map( NodeId::toString ).
                forEach( deletedNodeIds::add );
        }
        else
        {
            Arrays.stream( keys ).
                map( this::deleteByKey ).
                flatMap( nodeIds -> nodeIds.stream() ).
                map( NodeId::toString ).
                forEach( deletedNodeIds::add );
        }
        return deletedNodeIds.build();
    }

    private NodeIds deleteByKey( final String key )
    {
        if ( key.startsWith( "/" ) )
        {
            return deleteByPath( NodePath.create( key ).build() );
        }
        else
        {
            return deleteById( NodeId.from( key ) );
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

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setKeys( final String[] keys )
    {
        this.keys = keys;
    }
}
