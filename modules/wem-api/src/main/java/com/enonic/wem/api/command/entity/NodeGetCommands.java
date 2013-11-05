package com.enonic.wem.api.command.entity;


import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;

public class NodeGetCommands
{
    public GetNodeById byId( final EntityId id )
    {
        return new GetNodeById( id );
    }

    public GetNodesByIds byIds( final EntityIds ids )
    {
        return new GetNodesByIds( ids );
    }

    public GetNodeByPath byPath( final NodePath path )
    {
        return new GetNodeByPath( path );
    }

    public GetNodesByPaths byPaths( final NodePaths paths )
    {
        return new GetNodesByPaths( paths );
    }

    public GetNodesByParent byParent( final NodePath parent )
    {
        return new GetNodesByParent( parent );
    }
}
