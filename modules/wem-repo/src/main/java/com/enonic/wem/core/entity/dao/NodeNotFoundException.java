package com.enonic.wem.core.entity.dao;

import com.enonic.wem.api.exception.NotFoundException;

public class NodeNotFoundException
    extends NotFoundException
{
    public NodeNotFoundException( final String message )
    {
        super( message );
    }
}
