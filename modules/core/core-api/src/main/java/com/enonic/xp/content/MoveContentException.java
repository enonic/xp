package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class MoveContentException
    extends RuntimeException
{

    public MoveContentException( final String message )
    {
        super( message );
    }
}
