package com.enonic.xp.core.impl.content.validate;

import com.enonic.xp.content.DataValidationError;

public final class SiteConfigValidationError
    extends DataValidationError
{
    public SiteConfigValidationError( final String appName )
    {
        super( null, "Data validation error in site config for {0}", appName );
    }
}
