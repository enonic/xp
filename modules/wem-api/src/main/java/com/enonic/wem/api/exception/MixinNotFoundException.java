package com.enonic.wem.api.exception;

import com.enonic.wem.api.schema.mixin.QualifiedMixinName;

public final class MixinNotFoundException
    extends BaseException
{
    public MixinNotFoundException( final QualifiedMixinName qualifiedMixinName )
    {
        super( "Mixin [{0}] was not found", qualifiedMixinName );
    }
}
