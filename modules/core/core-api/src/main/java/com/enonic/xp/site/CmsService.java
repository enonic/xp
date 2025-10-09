package com.enonic.xp.site;

import com.enonic.xp.app.ApplicationKey;

public interface CmsService
{
    CmsDescriptor getDescriptor( ApplicationKey applicationKey );
}
