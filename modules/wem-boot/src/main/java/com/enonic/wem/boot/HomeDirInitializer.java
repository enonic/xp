package com.enonic.wem.boot;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Set;

import javax.servlet.ServletContext;

import com.enonic.wem.core.home.HomeDir;

final class HomeDirInitializer
{
    private final static String PATH_PREFIX = "/WEB-INF/home";

    private final ServletContext context;

    private final HomeDir homeDir;

    public HomeDirInitializer( final ServletContext context, final HomeDir homeDir )
    {
        this.context = context;
        this.homeDir = homeDir;
    }

    public void initialize()
        throws Exception
    {
        copyAll( this.homeDir.toFile(), PATH_PREFIX );
    }

    private void copyAll( final File to, final String from )
        throws Exception
    {
        copyFile( to, from );
        copyDir( to, from );
    }

    private void copyFile( final File to, final String from )
        throws Exception
    {
        final InputStream in = this.context.getResourceAsStream( from );
        if ( in == null )
        {
            return;
        }

        if ( to.exists() )
        {
            return;
        }

        Files.createDirectories( to.toPath().getParent() );
        Files.copy( in, to.toPath() );
    }

    private void copyDir( final File to, final String from )
        throws Exception
    {
        final Set<String> paths = this.context.getResourcePaths( from );
        if ( paths == null )
        {
            return;
        }

        for ( final String path : paths )
        {
            copyAll( new File( this.homeDir.toFile(), path.substring( PATH_PREFIX.length() ) ), path );
        }
    }
}
