package com.enonic.xp.server.internal.config;

import java.io.File;

public interface ConfigInstaller
{
    void updateConfig( File file );

    void deleteConfig( String fileName );
}
