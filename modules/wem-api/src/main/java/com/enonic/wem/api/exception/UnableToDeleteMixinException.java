package com.enonic.wem.api.exception;

import com.enonic.wem.api.schema.mixin.MixinName;

public final class UnableToDeleteMixinException
    extends BaseException
{
    public UnableToDeleteMixinException( final MixinName mixinName, final String reason )
    {
        super( "Unable to delete Mixin [{0}]: " + reason, mixinName );
    }
}
