package com.enonic.wem.api.schema.mixin;

import com.enonic.wem.api.exception.BaseException;

public final class MixinAlreadyExistException
    extends BaseException
{
    public MixinAlreadyExistException( final MixinName name )
    {
        super( "Mixin [{0}] already exists", name );
    }
}
