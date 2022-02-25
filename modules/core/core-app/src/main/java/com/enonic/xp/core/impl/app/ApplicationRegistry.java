package com.enonic.xp.core.impl.app;

import java.util.List;

import org.osgi.framework.Bundle;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.Configuration;

public interface ApplicationRegistry
{
    Application get( ApplicationKey key );

    List<Application> getAll();

    Application installApplication( Bundle bundle );

    void uninstallApplication( ApplicationKey applicationKey );

    void stopApplication( ApplicationKey applicationKey );

    boolean startApplication( ApplicationKey applicationKey, boolean throwOnInvalidVersion );

    void configureApplication( Bundle bundle, Configuration configuration );
}
