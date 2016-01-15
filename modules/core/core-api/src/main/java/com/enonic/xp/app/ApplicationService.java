package com.enonic.xp.app;

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

    void startApplication( ApplicationKey key );

    void stopApplication( ApplicationKey key );

    Application installApplication( final ByteSource byteSource );

    Application installApplication( final NodeId nodeId );

}
