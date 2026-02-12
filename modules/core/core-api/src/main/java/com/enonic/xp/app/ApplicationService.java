package com.enonic.xp.app;

import com.google.common.io.ByteSource;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface ApplicationService
{
    Application getInstalledApplication( ApplicationKey key )
        throws ApplicationNotFoundException;

    Application get( ApplicationKey key );

    Applications getInstalledApplications();

    Applications list();

    boolean isLocalApplication( ApplicationKey key );

    void startApplication( ApplicationKey key );

    void stopApplication( ApplicationKey key );

    Application installGlobalApplication( ByteSource byteSource );

    Application installLocalApplication( ByteSource byteSource );

    void uninstallApplication( ApplicationKey key );

    void installAllStoredApplications();

    Application createVirtualApplication( CreateVirtualApplicationParams params );

    boolean deleteVirtualApplication( ApplicationKey key );

    ApplicationMode getApplicationMode( ApplicationKey applicationKey );
}
