package com.enonic.xp.app;

import java.net.URL;

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

    void startApplication( ApplicationKey key, boolean unused );

    void stopApplication( ApplicationKey key, boolean unused );

    Application installGlobalApplication( URL url );

    Application installGlobalApplication( URL url, byte[] sha512 );

    Application installGlobalApplication( ByteSource byteSource, String unused );

    Application installLocalApplication( ByteSource byteSource, String unused );

    void uninstallApplication( ApplicationKey key, boolean unused );

    void installAllStoredApplications();

    Application createVirtualApplication( CreateVirtualApplicationParams params );

    boolean deleteVirtualApplication( ApplicationKey key );

    ApplicationMode getApplicationMode( ApplicationKey applicationKey );
}
