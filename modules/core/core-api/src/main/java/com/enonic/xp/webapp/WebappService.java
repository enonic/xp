package com.enonic.xp.webapp;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;

@PublicApi
public interface WebappService
{
    WebappDescriptor getDescriptor( ApplicationKey applicationKey );
}
