package com.enonic.xp.admin.impl.rest.resource.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.BaseException;

@Beta
public final class ContentQueryJsonConvertException
    extends BaseException
{
    public ContentQueryJsonConvertException( final String message )
    {
        super( message );
    }
}
