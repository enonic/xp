package com.enonic.xp.app;

import java.io.InputStream;

import com.google.common.annotations.Beta;

@Beta
public interface ApplicationService
{
    Application getApplication( ApplicationKey key )
        throws ApplicationNotFoundException;

    ApplicationKeys getApplicationKeys();

    Applications getAllApplications();

    void startApplication( ApplicationKey key );

    void stopApplication( ApplicationKey key );

    Application installApplication( final InputStream inputStream );

}
