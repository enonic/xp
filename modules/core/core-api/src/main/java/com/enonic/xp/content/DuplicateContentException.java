package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class DuplicateContentException
    extends RuntimeException
{

    public DuplicateContentException( final String message )
    {
        super( message );
    }
}
