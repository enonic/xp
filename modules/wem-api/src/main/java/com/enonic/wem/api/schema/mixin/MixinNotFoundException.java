package com.enonic.wem.api.schema.mixin;

import com.enonic.wem.api.exception.BaseException;

public final class MixinNotFoundException
    extends BaseException
{
    public MixinNotFoundException( final MixinName mixinName )
    {
        super( "Mixin [{0}] was not found", mixinName );
    }
}
