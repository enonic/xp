package com.enonic.xp.core.impl.app;

import java.util.List;

import org.osgi.framework.Bundle;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.Configuration;

public interface ApplicationRegistry
{
    Application get( ApplicationKey key );

    List<Application> getAll();

    Application install( ApplicationKey applicationKey, ByteSource byteSource );

    void uninstall( ApplicationKey applicationKey );

    void stop( ApplicationKey applicationKey );

    void start( ApplicationKey applicationKey );

    void configure( Bundle bundle, Configuration configuration );
}
