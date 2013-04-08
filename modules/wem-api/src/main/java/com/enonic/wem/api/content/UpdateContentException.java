package com.enonic.wem.api.content;


import com.enonic.wem.api.exception.BaseException;

public class UpdateContentException
    extends BaseException
{
    public UpdateContentException( final String message, final Throwable t )
    {
        super( message, t );
    }
}
