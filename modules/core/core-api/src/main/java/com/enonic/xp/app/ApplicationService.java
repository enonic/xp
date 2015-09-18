package com.enonic.xp.app;

import com.google.common.annotations.Beta;

@Beta
public interface ApplicationService
{
    Application getApplication( ApplicationKey key )
        throws ApplicationNotFoundException;

    Applications getApplications( ApplicationKeys keys );

    Applications getAllApplications();

    ClassLoader getClassLoader(Application application );

    void startApplication( ApplicationKey key );

    void stopApplication( ApplicationKey key );
}
