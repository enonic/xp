package com.enonic.xp.app;

import com.google.common.annotations.Beta;

@Beta
public interface ApplicationService
{
    Application getModule( ApplicationKey key )
        throws ApplicationNotFoundException;

    Applications getModules( ApplicationKeys keys );

    Applications getAllModules();

    ClassLoader getClassLoader(Application application );

    void startModule( ApplicationKey key );

    void stopModule( ApplicationKey key );
}
