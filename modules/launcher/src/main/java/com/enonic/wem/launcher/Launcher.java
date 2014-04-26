package com.enonic.wem.launcher;

import com.enonic.wem.launcher.config.ConfigLoader;
import com.enonic.wem.launcher.config.ConfigProperties;
import com.enonic.wem.launcher.home.HomeDir;
import com.enonic.wem.launcher.home.HomeResolver;
import com.enonic.wem.launcher.util.SystemProperties;

public final class Launcher
{
    public static void main( final String... args )
        throws Exception
    {
        final SystemProperties systemProperties = SystemProperties.getDefault();
        systemProperties.put( "wem.home", "." );

        final HomeResolver resolver = new HomeResolver( systemProperties );
        final HomeDir homeDir = resolver.resolve();

        final ConfigLoader configLoader = new ConfigLoader( homeDir );
        final ConfigProperties props = configLoader.load();
        props.putAll( systemProperties );

        System.out.println( props.interpolate() );
    }
}
