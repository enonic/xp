package com.enonic.xp.core.impl.site;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.site.SiteDescriptor;

public interface SiteDescriptorRegistry
{
    SiteDescriptor get( ModuleKey key );
}
