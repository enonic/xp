package com.enonic.xp.core.impl.app;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

import com.enonic.xp.app.Application;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;

public interface ApplicationAdaptor
    extends Application
{
    ApplicationUrlResolver getUrlResolver();

    void setConfig( Configuration config );

    Bundle getBundle();

    ServiceRegistration<Application> getRegistration();

    void setRegistration( ServiceRegistration<Application> registration );
}
