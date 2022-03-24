package com.enonic.xp.core.impl.app;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.site.SiteDescriptor;

public interface DynamicSchemaServiceInternal
{
    SiteDescriptor createSite( ApplicationKey key, String resource );

    boolean deleteSite( ApplicationKey applicationKey );
}
