package com.enonic.xp.node;

import com.google.common.annotations.Beta;

@Beta
public class MoveNodeException
    extends RuntimeException
{
    public MoveNodeException( final String message )
    {
        super( message );
    }
}
