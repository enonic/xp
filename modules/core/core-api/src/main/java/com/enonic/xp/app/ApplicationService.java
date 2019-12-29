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

    ApplicationKeys getInstalledApplicationKeys();

    Applications getInstalledApplications();

    boolean isLocalApplication( ApplicationKey key );

    void startApplication( ApplicationKey key, final boolean triggerEvent );

    void stopApplication( ApplicationKey key, final boolean triggerEvent );

    Application installGlobalApplication( final URL url );

    Application installGlobalApplication( final ByteSource byteSource, final String applicationName );

    Application installLocalApplication( final ByteSource byteSource, final String applicationName );

    @Deprecated
    Application installStoredApplication( final NodeId nodeId );

    Application installStoredApplication( final NodeId nodeId, final ApplicationInstallationParams params );

    void uninstallApplication( final ApplicationKey key, final boolean triggerEvent );

    void publishUninstalledEvent( final ApplicationKey key );

    @Deprecated
    void invalidate( ApplicationKey key );

    void invalidate( ApplicationKey key, ApplicationInvalidationLevel level );

    @Deprecated
    void installAllStoredApplications();

    void installAllStoredApplications( final ApplicationInstallationParams params );
}
