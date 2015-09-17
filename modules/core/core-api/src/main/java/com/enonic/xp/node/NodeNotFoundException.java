package com.enonic.xp.node;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.NotFoundException;

@Beta
public class NodeNotFoundException
    extends NotFoundException
{
    public NodeNotFoundException( final String message )
    {
        super( message );
    }
}
