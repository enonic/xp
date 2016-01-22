package com.enonic.xp.script.impl.function;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.google.common.io.Files;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;

import static org.junit.Assert.*;

public class ResourceResolverTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ResourceService resourceService;

    @Before
    public void setup()
    {
        this.resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( this.resourceService.getResource( Mockito.any() ) ).then( this::loadResource );
    }

    private Resource loadResource( final InvocationOnMock invocation )
        throws Exception
    {
        return loadResource( (ResourceKey) invocation.getArguments()[0] );
    }

    private Resource loadResource( final ResourceKey key )
        throws Exception
    {
        final File file = new File( this.temporaryFolder.getRoot(), key.getPath() );
        return new UrlResource( key, file.toURI().toURL() );
    }

    private void touchFile( final String path )
        throws Exception
    {
        final File file = new File( this.temporaryFolder.getRoot(), path );
        file.getParentFile().mkdirs();
        Files.touch( file );
    }

    @Test
    public void resolve_absolute()
        throws Exception
    {
        touchFile( "dummy.txt" );

        final ResourceKey key = resolve( "/a/b/c.txt", "/dummy.txt" );
        assertEquals( "/dummy.txt", key.getPath() );
    }

    @Test
    public void resolve_relative()
        throws Exception
    {
        touchFile( "dir1/dir2/dummy.txt" );
        touchFile( "dir1/dummy.txt" );

        final ResourceKey key1 = resolve( "/dir1/dir2/c.txt", "./dummy.txt" );
        assertEquals( "/dir1/dir2/dummy.txt", key1.getPath() );

        final ResourceKey key2 = resolve( "/dir1/dir2/c.txt", "../dummy.txt" );
        assertEquals( "/dir1/dummy.txt", key2.getPath() );

        final ResourceKey key3 = resolve( "/dir1/dir2/c.txt", "dummy.txt" );
        assertEquals( "/dir1/dir2/dummy.txt", key3.getPath() );
    }

    @Test
    public void resolve_compatible()
        throws Exception
    {
        touchFile( "/site/dummy.txt" );

        final ResourceKey key = resolve( "/a/b/c.txt", "/dummy.txt" );
        assertEquals( "/site/dummy.txt", key.getPath() );
    }

    @Test
    public void resolveJs_absolute()
        throws Exception
    {
        touchFile( "script.js" );
        touchFile( "dir1/index.js" );

        final ResourceKey key1 = resolveJs( "/a/b/c.txt", "/script.js" );
        assertEquals( "/script.js", key1.getPath() );

        final ResourceKey key2 = resolveJs( "/a/b/c.txt", "/script" );
        assertEquals( "/script.js", key2.getPath() );

        final ResourceKey key3 = resolveJs( "/a/b/c.txt", "/dir1" );
        assertEquals( "/dir1/index.js", key3.getPath() );
    }

    @Test
    public void resolveJs_relative()
        throws Exception
    {
        touchFile( "script.js" );
        touchFile( "dir1/index.js" );
        touchFile( "dir1/script.js" );

        final ResourceKey key1 = resolveJs( "/dir1/c.txt", "./script.js" );
        assertEquals( "/dir1/script.js", key1.getPath() );

        final ResourceKey key2 = resolveJs( "/dir1/c.txt", "./script" );
        assertEquals( "/dir1/script.js", key2.getPath() );

        final ResourceKey key3 = resolveJs( "/dir1/c.txt", "." );
        assertEquals( "/dir1/index.js", key3.getPath() );

        final ResourceKey key4 = resolveJs( "/dir1/c.txt", "../script.js" );
        assertEquals( "/script.js", key4.getPath() );
    }

    @Test
    public void resolveJs_compatible()
        throws Exception
    {
        touchFile( "/site/script.js" );

        final ResourceKey key = resolveJs( "/a/b/c.txt", "/script.js" );
        assertEquals( "/site/script.js", key.getPath() );
    }

    @Test
    public void resolveJs_scan()
        throws Exception
    {
        final ResourceKey key1 = resolveJs( "/a/b/c.txt", "script1.js" );
        assertEquals( "/lib/script1.js", key1.getPath() );

        touchFile( "a/b/script2.js" );
        final ResourceKey key2 = resolveJs( "/a/b/c.txt", "script2.js" );
        assertEquals( "/a/b/script2.js", key2.getPath() );

        touchFile( "a/b/lib/script3.js" );
        final ResourceKey key3 = resolveJs( "/a/b/c.txt", "script3.js" );
        assertEquals( "/a/b/lib/script3.js", key3.getPath() );

        touchFile( "a/lib/script4.js" );
        final ResourceKey key4 = resolveJs( "/a/b/c.txt", "script4.js" );
        assertEquals( "/a/lib/script4.js", key4.getPath() );

        touchFile( "a/lib/script4.js" );
        final ResourceKey key5 = resolveJs( "/a/b/c.txt", "script4" );
        assertEquals( "/a/lib/script4.js", key5.getPath() );
    }

    private ResourceKey resolve( final String base, final String path )
    {
        return resolver( base ).resolve( path );
    }

    private ResourceKey resolveJs( final String base, final String path )
    {
        return resolver( base ).resolveJs( path );
    }

    private ResourceResolver resolver( final String base )
    {
        return new ResourceResolver( this.resourceService, ResourceKey.from( "myapp:" + base ) );
    }
}
