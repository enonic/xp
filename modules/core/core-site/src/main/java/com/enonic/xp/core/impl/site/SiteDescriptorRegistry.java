package com.enonic.xp.core.impl.site;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.site.SiteDescriptor;

public interface SiteDescriptorRegistry
{
    SiteDescriptor get( ApplicationKey key );
}
