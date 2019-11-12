package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class PublishContentException
    extends RuntimeException
{

    public PublishContentException( final String message )
    {
        super( message );
    }
}

