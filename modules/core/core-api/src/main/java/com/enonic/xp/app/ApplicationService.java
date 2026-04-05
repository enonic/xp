package com.enonic.xp.app;

import com.google.common.io.ByteSource;


public interface ApplicationService
{
    Application getInstalledApplication( ApplicationKey key );

    Application get( ApplicationKey key );

    Applications getInstalledApplications();

    Applications list();

    boolean isLocalApplication( ApplicationKey key );

    void startApplication( ApplicationKey key );

    void stopApplication( ApplicationKey key );

    Application installGlobalApplication( ByteSource byteSource );

    Application installLocalApplication( ByteSource byteSource );

    void uninstallApplication( ApplicationKey key );

    void uninstallLocalApplication( ApplicationKey key );

    void installAllStoredApplications();

    Application createVirtualApplication( CreateVirtualApplicationParams params );

    boolean deleteVirtualApplication( ApplicationKey key );

    ApplicationMode getApplicationMode( ApplicationKey applicationKey );
}
