package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.BaseException;

@Beta
public class ContentDataValidationException
    extends BaseException
{
    public ContentDataValidationException( final String message )
    {
        super( message );
    }
}
