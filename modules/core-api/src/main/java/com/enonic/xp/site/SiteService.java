package com.enonic.xp.site;

import com.enonic.xp.module.ModuleKey;

public interface SiteService
{
    SiteDescriptor getDescriptor( ModuleKey moduleKey );
}
