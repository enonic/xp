package com.enonic.wem.api.vfs;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class LocalFileTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void special_characters()
        throws Exception
    {
        final File root = temporaryFolder.getRoot();

        final String fileName = "æøåéâí";
        final File file = new File( root, fileName );

        final VirtualFile vf = VirtualFiles.from( file.toPath() );

        assertEquals( fileName, vf.getName() );
        final URL url = vf.getUrl();

        final String urlDecoded = urlDecode( url.getFile() );
        final String expectedFileName = Paths.get( root.toPath().toString(), fileName ).toString();

        assertEquals( expectedFileName, urlDecoded );
    }

    @Test
    public void whitespace()
        throws Exception
    {
        final File root = temporaryFolder.getRoot();

        final String fileName = "this has whitespaces";
        final File file = new File( root, fileName );

        final VirtualFile vf = VirtualFiles.from( file.toPath() );

        assertEquals( fileName, vf.getName() );
        final URL url = vf.getUrl();

        final String urlDecoded = urlDecode( url.getFile() );
        final String expectedFileName = Paths.get( root.toPath().toString(), fileName ).toString();

        assertEquals( expectedFileName, urlDecoded );
    }


    private String urlDecode( final String encoded )
        throws Exception
    {
        return URLDecoder.decode( encoded, "UTF-8" );
    }

}