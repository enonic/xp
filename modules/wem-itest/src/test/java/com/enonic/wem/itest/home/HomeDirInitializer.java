package com.enonic.wem.itest.home;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import com.enonic.wem.core.home.HomeDir;

public final class HomeDirInitializer
{
    private final HomeDir homeDir;

    public HomeDirInitializer()
        throws Exception
    {
        this.homeDir = new HomeDir( Files.createTempDir() );
        copyFile( "config/cms.properties" );
    }

    public void destroy()
    {
        FileUtils.deleteQuietly( this.homeDir.toFile() );
    }

    private void copyFile( final String resource )
        throws IOException
    {
        final String from = "/homeDir/" + resource;
        final File to = new File( this.homeDir.toFile(), resource );

        final URL url = getClass().getResource( from );
        if ( url == null )
        {
            throw new IOException( "Resource [" + from + "] not found" );
        }

        Files.createParentDirs( to );
        Files.copy( Resources.newInputStreamSupplier( url ), to );
    }
}
