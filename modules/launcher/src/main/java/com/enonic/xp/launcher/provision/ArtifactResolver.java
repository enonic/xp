package com.enonic.xp.launcher.provision;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import com.enonic.xp.launcher.SharedConstants;

final class ArtifactResolver
    implements SharedConstants
{
    private static final Pattern GAV_PATTERN = Pattern.compile( "([^/ ]+)/([^/ ]+)/([^/ ]*)(/([^/ ]+)(/([^/ ]+))?)?" );

    private final List<File> repoDirs;

    public ArtifactResolver()
    {
        this.repoDirs = Lists.newArrayList();
    }

    public void addRepo( final File repoDir )
    {
        this.repoDirs.add( repoDir );
    }

    public String resolve( final String uri )
    {
        for ( final File bundleDir : this.repoDirs )
        {
            File file = findFile( bundleDir, uri );
            if ( file != null )
            {
                return file.toURI().toString();
            }
        }

        return null;
    }

    private static File findFile( final File dir, String uri )
    {
        final String path = fromMaven( uri );
        final File theFile = new File( dir, path );
        if ( theFile.exists() && !theFile.isDirectory() )
        {
            return theFile;
        }

        return null;
    }

    private static String fromMaven( final String name )
    {
        final Matcher m = GAV_PATTERN.matcher( name );
        if ( !m.matches() )
        {
            return name;
        }

        final String groupId = m.group( 1 );
        final String artifactId = m.group( 2 );
        final String version = m.group( 3 );
        final String extension = m.group( 5 );
        final String classifier = m.group( 7 );

        final StringBuilder path = new StringBuilder();
        path.append( groupId.replace( ".", "/" ) ).append( "/" );
        path.append( artifactId ).append( "/" );
        path.append( version ).append( "/" );
        path.append( artifactId ).append( "-" ).append( version );

        if ( !Strings.isNullOrEmpty( classifier ) )
        {
            path.append( "-" ).append( classifier );
        }

        if ( !Strings.isNullOrEmpty( extension ) )
        {
            path.append( "." ).append( extension );
        }
        else
        {
            path.append( ".jar" );
        }

        return path.toString();
    }
}
