package com.enonic.xp.app;

import java.net.URL;

import com.google.common.io.ByteSource;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.node.NodeId;

@PublicApi
public interface ApplicationService
{
    Application getInstalledApplication( ApplicationKey key )
        throws ApplicationNotFoundException;

    Application getApplication( ApplicationKey key );

    @Deprecated
    ApplicationKeys getInstalledApplicationKeys();

    Applications getInstalledApplications();

    Applications getAllApplications();

    boolean isLocalApplication( ApplicationKey key );

    void startApplication( ApplicationKey key, boolean triggerEvent );

    void stopApplication( ApplicationKey key, boolean triggerEvent );

    Application installGlobalApplication( URL url );

    Application installGlobalApplication( URL url, byte[] sha512 );

    Application installGlobalApplication( ByteSource byteSource, String applicationName );

    Application installLocalApplication( ByteSource byteSource, String applicationName );

    @Deprecated
    Application installStoredApplication( NodeId nodeId );

    Application installStoredApplication( NodeId nodeId, ApplicationInstallationParams params );

    void uninstallApplication( ApplicationKey key, boolean triggerEvent );

    @Deprecated
    void publishUninstalledEvent( ApplicationKey key );

    @Deprecated
    void invalidate( ApplicationKey key );

    @Deprecated
    void invalidate( ApplicationKey key, ApplicationInvalidationLevel level );

    @Deprecated
    void installAllStoredApplications();

    void installAllStoredApplications( ApplicationInstallationParams params );

}
