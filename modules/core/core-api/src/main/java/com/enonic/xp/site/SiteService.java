package com.enonic.xp.site;

import com.enonic.xp.app.ApplicationKey;

public interface SiteService
{
    SiteDescriptor getDescriptor( ApplicationKey applicationKey );
}
