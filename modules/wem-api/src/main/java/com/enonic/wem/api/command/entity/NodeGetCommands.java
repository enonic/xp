package com.enonic.wem.api.command.entity;


import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NodePath;

public class NodeGetCommands
{
    public GetNodeById byId( final EntityId id )
    {
        return new GetNodeById( id );
    }

    public GetNodeByPath byPath( final NodePath path )
    {
        return new GetNodeByPath( path );
    }

    public GetNodesByParent byParent( final NodePath parent )
    {
        return new GetNodesByParent( parent );
    }
}
