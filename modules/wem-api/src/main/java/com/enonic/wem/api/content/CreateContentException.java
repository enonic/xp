package com.enonic.wem.api.content;


import com.enonic.wem.api.exception.BaseException;

public class CreateContentException
    extends BaseException
{
    public CreateContentException( final String message, final Throwable t )
    {
        super( message, t );
    }
}
