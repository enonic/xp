package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.NotFoundException;

@PublicApi
public class NodeNotFoundException
    extends NotFoundException
{
    public NodeNotFoundException( final String message )
    {
        super( message );
    }
}
