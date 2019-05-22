package com.enonic.xp.app;

import java.net.URL;

import com.google.common.annotations.Beta;
import com.google.common.io.ByteSource;

import com.enonic.xp.node.NodeId;

@Beta
public interface ApplicationService
{
    Application getInstalledApplication( ApplicationKey key )
        throws ApplicationNotFoundException;

    ApplicationKeys getInstalledApplicationKeys();

    Applications getInstalledApplications();

    boolean isLocalApplication( ApplicationKey key );

    void startApplication( ApplicationKey key, final boolean triggerEvent );

    void stopApplication( ApplicationKey key, final boolean triggerEvent );

    @Deprecated
    Application installGlobalApplication( final URL url );

    Application installGlobalApplication( final URL url, final boolean triggerEvent );

    @Deprecated
    Application installGlobalApplication( final ByteSource byteSource, final String applicationName);

    Application installGlobalApplication( final ByteSource byteSource, final String applicationName, final boolean triggerEvent );

    Application installLocalApplication( final ByteSource byteSource, final String applicationName );

    @Deprecated
    Application installStoredApplication( final NodeId nodeId);

    Application installStoredApplication( final NodeId nodeId, final boolean start, final boolean triggerEvent );

    void uninstallApplication( final ApplicationKey key, final boolean triggerEvent );

    void publishUninstalledEvent( final ApplicationKey key );

    void invalidate( ApplicationKey key );

    @Deprecated
    void installAllStoredApplications();

    void installAllStoredApplications( final boolean triggerEvent );
}
