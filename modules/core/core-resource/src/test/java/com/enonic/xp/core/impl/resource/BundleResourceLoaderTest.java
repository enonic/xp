package com.enonic.xp.core.impl.resource;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;

import static org.junit.Assert.*;

public class BundleResourceLoaderTest
    extends ResourceLoaderTest
{
    @Override
    protected void initialize()
    {
        this.loader = new BundleResourceLoader();
    }

    @Test
    public void getResource()
        throws Exception
    {
        final URL url = mockResource( "/a/b/resource.txt" );

        final Resource resource = this.loader.getResource( this.app, ResourceKey.from( this.appKey, "/a/b/resource.txt" ) );
        assertNotNull( resource );
        assertTrue( resource.exists() );
        assertEquals( url, resource.getUrl() );
    }

    @Test
    public void findFilders()
        throws Exception
    {
        Mockito.when( this.bundle.getEntryPaths( "/site/pages" ) ).thenReturn(
            Collections.enumeration( Arrays.asList( "/default/", "/rss/", "/readme.md", "/person_default_page.jpg" ) ) );

        final ResourceKeys keys = this.loader.findFolders( this.app, "/site/pages" );
        assertNotNull( keys );
        assertTrue( keys.isNotEmpty() );
        assertEquals( 2, keys.getSize() );
        assertTrue( keys.contains( ResourceKey.from( this.appKey, "rss" ) ) );
        assertTrue( keys.contains( ResourceKey.from( this.appKey, "default" ) ) );
    }

    private URL mockResource( final String path )
        throws Exception
    {
        final File file = this.temporaryFolder.newFile();
        final URL url = file.toURI().toURL();
        Mockito.when( this.bundle.getResource( path ) ).thenReturn( url );
        return url;
    }
}
