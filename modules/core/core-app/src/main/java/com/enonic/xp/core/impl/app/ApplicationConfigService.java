package com.enonic.xp.core.impl.app;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.Configuration;

public interface ApplicationConfigService
{
    void setConfiguration( ApplicationKey key, Configuration configuration );
}
