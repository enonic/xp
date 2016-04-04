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

    Application installGlobalApplication( final URL url );

    Application installGlobalApplication( final ByteSource byteSource );

    Application installLocalApplication( final ByteSource byteSource );

    Application installStoredApplication( final NodeId nodeId );

    void uninstallApplication( final ApplicationKey key, final boolean triggerEvent );

}
