package com.enonic.xp.launcher.impl.env;

import java.io.File;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.enonic.xp.launcher.impl.SharedConstants;

public final class EnvironmentResolver
    implements SharedConstants
{
    private final SystemProperties properties;

    public EnvironmentResolver( final SystemProperties properties )
    {
        this.properties = properties;
    }

    public Environment resolve()
    {
        final EnvironmentImpl env = new EnvironmentImpl();
        env.installDir = resolveInstallDir();
        env.homeDir = resolveHomeDir( env.installDir );
        return env;
    }

    private File resolveInstallDir()
    {
        final String path = this.properties.get( XP_INSTALL_DIR );
        if ( path == null || path.isEmpty() )
        {
            return null;
        }

        return new File( path );
    }

    private File resolveHomeDir( final File installDir )
    {
        final String propValue = Objects.requireNonNullElse( this.properties.get( XP_HOME_DIR ), "" );
        final String envValue = Objects.requireNonNullElse( this.properties.getEnv( XP_HOME_DIR_ENV ), "" );

        return Stream.of( propValue, envValue ).filter( Predicate.not( String::isEmpty ) ).findFirst().
            map( File::new ).orElseGet( () -> installDir != null ? new File( installDir, "home" ) : null );
    }
}
