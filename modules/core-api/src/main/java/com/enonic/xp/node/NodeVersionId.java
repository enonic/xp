package com.enonic.xp.node;

import com.enonic.xp.support.AbstractId;

public class NodeVersionId
    extends AbstractId
{
    private NodeVersionId( final String value )
    {
        super( value );
    }

    public static NodeVersionId from( final String value )
    {
        return new NodeVersionId( value );
    }
}
