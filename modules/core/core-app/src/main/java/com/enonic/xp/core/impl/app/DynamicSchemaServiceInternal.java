package com.enonic.xp.core.impl.app;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.site.SiteDescriptor;

public interface DynamicSchemaServiceInternal
{
    DynamicSchemaResult<SiteDescriptor> createSite( CreateDynamicSiteParams params );

    boolean deleteSite( ApplicationKey applicationKey );
}
