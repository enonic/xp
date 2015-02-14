package com.enonic.xp.core.node;

import com.enonic.xp.core.exception.NotFoundException;

public class NodeNotFoundException
    extends NotFoundException
{
    public NodeNotFoundException( final String message )
    {
        super( message );
    }
}
