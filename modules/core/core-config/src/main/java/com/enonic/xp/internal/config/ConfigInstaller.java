package com.enonic.xp.internal.config;

import java.io.File;

public interface ConfigInstaller
{
    void updateConfig( File file );

    void deleteConfig( String fileName );
}
