package com.enonic.xp.webapp;

import com.enonic.xp.app.ApplicationKey;


public interface WebappService
{
    WebappDescriptor getDescriptor( ApplicationKey applicationKey );
}
