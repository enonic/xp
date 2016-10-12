package com.enonic.xp.lib.node;

import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;

public final class GetNodeHandler
    extends BaseContextHandler
{
    private String key;

    @Override
    protected Object doExecute()
    {
        if ( this.key.startsWith( "/" ) )
        {
            return getByPath( NodePath.create( this.key ).build() );
        }
        else
        {
            return getById( NodeId.from( this.key ) );
        }
    }

    private NodeMapper getByPath( final NodePath key )
    {
        try
        {
            return convert( this.nodeService.getByPath( key ) );
        }
        catch ( final NodeNotFoundException e )
        {
            return null;
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

    private NodeMapper convert( final Node content )
    {
        return new NodeMapper( content );
    }

    public void setKey( final String key )
    {
        this.key = key;
    }
}
