package com.enonic.xp.launcher.impl.env;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.enonic.xp.launcher.impl.SharedConstants;

public final class EnvironmentResolver
{
    private final SystemProperties properties;

    public EnvironmentResolver( final SystemProperties properties )
    {
        this.properties = properties;
    }

    public Environment resolve()
    {
        final Path installDir = resolveInstallDir();
        final Path homeDir = resolveHomeDir( installDir );
        return new EnvironmentImpl( installDir, homeDir );
    }

    private Path resolveInstallDir()
    {
        final String path = this.properties.get( SharedConstants.XP_INSTALL_DIR );
        if ( path == null || path.isEmpty() )
        {
            return null;
        }

        return Path.of( path );
    }

    private Path resolveHomeDir( final Path installDir )
    {
        final String propValue = Objects.requireNonNullElse( this.properties.get( SharedConstants.XP_HOME_DIR ), "" );
        final String envValue = Objects.requireNonNullElse( this.properties.getEnv( SharedConstants.XP_HOME_DIR_ENV ), "" );

        return Stream.of( propValue, envValue ).filter( Predicate.not( String::isEmpty ) ).findFirst().
            map( Path::of ).orElseGet( () -> installDir != null ? installDir.resolve( "home" ) : null );
    }
}
