package com.enonic.wem.api.exception;

import com.enonic.wem.api.content.schema.mixin.QualifiedMixinName;

public final class UnableToDeleteMixinException
    extends BaseException
{
    public UnableToDeleteMixinException( final QualifiedMixinName qualifiedMixinName, final String reason )
    {
        super( "Unable to delete Mixin [{0}]: " + reason, qualifiedMixinName );
    }
}
