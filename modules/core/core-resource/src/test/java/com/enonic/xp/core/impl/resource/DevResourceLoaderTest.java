package com.enonic.xp.core.impl.resource;

import java.io.File;
import java.nio.file.Files;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;

import static org.junit.Assert.*;

public class DevResourceLoaderTest
    extends ResourceLoaderTest
{
    @Override
    protected void initialize()
    {
        this.loader = new DevResourceLoader();
    }

    @Test
    public void getResource()
        throws Exception
    {
        Mockito.when( this.app.getSourcePaths() ).thenReturn( Lists.newArrayList( this.temporaryFolder.getRoot().getAbsolutePath() ) );

        final File folder = this.temporaryFolder.newFolder( "a", "b" );
        final File file = new File( folder, "resource.txt" );
        Files.write( file.toPath(), "hello".getBytes() );

        final Resource resource = this.loader.getResource( this.app, ResourceKey.from( this.appKey, "/a/b/resource.txt" ) );
        assertNotNull( resource );
        assertTrue( resource.exists() );
        assertEquals( file.toURI().toURL(), resource.getUrl() );
    }

    @Test
    public void getResource_fileNotFound()
        throws Exception
    {
        Mockito.when( this.app.getSourcePaths() ).thenReturn( Lists.newArrayList( this.temporaryFolder.getRoot().getAbsolutePath() ) );

        final Resource resource = this.loader.getResource( this.app, ResourceKey.from( this.appKey, "/a/b/resource.txt" ) );
        assertNotNull( resource );
        assertFalse( resource.exists() );
    }
}
