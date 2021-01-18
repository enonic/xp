package com.enonic.xp.core.impl.app;

import com.enonic.xp.app.Application;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;

public interface ApplicationAdaptor
    extends Application
{
    ApplicationUrlResolver getUrlResolver();

    void setConfig( Configuration config );
}
