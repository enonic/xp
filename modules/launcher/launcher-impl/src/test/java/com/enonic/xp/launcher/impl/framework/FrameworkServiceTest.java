package com.enonic.xp.launcher.impl.framework;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.launcher.impl.config.ConfigLoader;
import com.enonic.xp.launcher.impl.config.ConfigProperties;
import com.enonic.xp.launcher.impl.env.Environment;
import com.enonic.xp.launcher.impl.env.EnvironmentResolver;
import com.enonic.xp.launcher.impl.env.SystemProperties;

class FrameworkServiceTest
{
    @TempDir
    Path temporaryFolder;

    @Test
    void lifecycle()
        throws Exception
    {
        final SystemProperties systemProperties = new SystemProperties();
        systemProperties.put( "xp.install", temporaryFolder.toString() );
        final EnvironmentResolver environmentResolver = new EnvironmentResolver( systemProperties );
        final Environment env = environmentResolver.resolve();
        final ConfigProperties configProperties = new ConfigLoader( env ).load();
        configProperties.interpolate();
        final FrameworkService frameworkService = new FrameworkService( configProperties );

        frameworkService.start();

        frameworkService.reset();

        frameworkService.restart();

        frameworkService.stop();
    }
}
