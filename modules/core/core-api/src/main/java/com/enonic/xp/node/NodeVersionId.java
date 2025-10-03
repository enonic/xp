package com.enonic.xp.node;


import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class NodeVersionId
    extends UUID
{
    public NodeVersionId()
    {
        super();
    }

    private NodeVersionId( final Object object )
    {
        super( object );
    }

    public static NodeVersionId from( final String string )
    {
        return new NodeVersionId( string );
    }

    public static NodeVersionId from( final Object object )
    {
        return new NodeVersionId( object );
    }
}
