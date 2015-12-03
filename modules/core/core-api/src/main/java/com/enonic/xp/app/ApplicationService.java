package com.enonic.xp.app;

import com.google.common.annotations.Beta;

@Beta
public interface ApplicationService
{
    Application getApplication( ApplicationKey key )
        throws ApplicationNotFoundException;

    Applications getAllApplications();

    void startApplication( ApplicationKey key );

    void stopApplication( ApplicationKey key );
}
