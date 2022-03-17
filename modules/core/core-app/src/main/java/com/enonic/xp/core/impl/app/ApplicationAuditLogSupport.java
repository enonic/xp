package com.enonic.xp.core.impl.app;

import java.net.URL;

import com.enonic.xp.app.ApplicationKey;

public interface ApplicationAuditLogSupport
{
    void startApplication( ApplicationKey applicationKey );

    void stopApplication( ApplicationKey applicationKey );

    void installApplication( ApplicationKey applicationKey, URL url );

    void installApplication( ApplicationKey applicationKey );

    void uninstallApplication( ApplicationKey applicationKey );
}
