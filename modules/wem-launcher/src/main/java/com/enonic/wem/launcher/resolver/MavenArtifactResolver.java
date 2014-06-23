package com.enonic.wem.launcher.resolver;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.wem.launcher.LauncherException;

public final class MavenArtifactResolver
    implements ArtifactResolver
{
    private static final Pattern PATTERN = Pattern.compile( "([^/ ]+)/([^/ ]+)/([^/ ]*)(/([^/ ]+)(/([^/ ]+))?)?" );

    private final List<File> repoDirs;

    public MavenArtifactResolver( final List<File> repoDirs )
    {
        this.repoDirs = repoDirs;
    }

    @Override
    public File resolve( final String uri )
    {
        for ( final File dir : this.repoDirs )
        {
            final File file = findFile( dir, uri );
            if ( file != null )
            {
                return file;
            }
        }

        throw new LauncherException( "Could not resolve [%s]", uri );
    }

    private File findFile( final File dir, final String uri )
    {
        final String path = fromMaven( uri );
        final File file = new File( dir, path );
        if ( file.exists() && file.isFile() )
        {
            return file;
        }

        return null;
    }

    private String fromMaven( final String uri )
    {
        final Matcher m = PATTERN.matcher( uri );
        if ( !m.matches() )
        {
            return uri;
        }

        final StringBuilder path = new StringBuilder();
        path.append( m.group( 1 ).replace( ".", "/" ) );
        path.append( "/" );

        final String artifactId = m.group( 2 );
        final String version = m.group( 3 );
        final String extension = m.group( 5 );
        final String classifier = m.group( 7 );

        path.append( artifactId ).append( "/" );
        path.append( version ).append( "/" );
        path.append( artifactId ).append( "-" ).append( version );

        if ( !isNullOrEmpty( classifier ) )
        {
            path.append( "-" ).append( classifier );
        }
        if ( !isNullOrEmpty( extension ) )
        {
            path.append( "." ).append( extension );
        }
        else
        {
            path.append( ".jar" );
        }

        return path.toString();
    }

    private boolean isNullOrEmpty( final String str )
    {
        return ( str == null ) || str.isEmpty();
    }
}
