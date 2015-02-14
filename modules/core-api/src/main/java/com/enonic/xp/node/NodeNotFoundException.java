package com.enonic.xp.node;

import com.enonic.xp.exception.NotFoundException;

public class NodeNotFoundException
    extends NotFoundException
{
    public NodeNotFoundException( final String message )
    {
        super( message );
    }
}
