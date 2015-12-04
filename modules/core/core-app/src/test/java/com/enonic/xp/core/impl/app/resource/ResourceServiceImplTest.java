package com.enonic.xp.core.impl.app.resource;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.io.Files;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.impl.app.MockApplication;
import com.enonic.xp.core.impl.app.resolver.ClassLoaderApplicationUrlResolver;
import com.enonic.xp.resource.ResourceKeys;

import static org.junit.Assert.*;

public class ResourceServiceImplTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ApplicationKey appKey;

    private ResourceServiceImpl resourceService;

    private File appDir;

    @Before
    public void setup()
        throws Exception
    {
        this.appDir = this.temporaryFolder.newFolder( "myapp" );

        this.appKey = ApplicationKey.from( "myapp" );
        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );

        final MockApplication app = new MockApplication();
        app.setStarted( true );
        app.setUrlResolver( ClassLoaderApplicationUrlResolver.create( this.appDir.toURI().toURL() ) );

        Mockito.when( applicationService.getApplication( this.appKey ) ).thenReturn( app );

        this.resourceService = new ResourceServiceImpl();
        this.resourceService.setApplicationService( applicationService );
    }

    private void newFile( final String name )
        throws Exception
    {
        final File file = new File( this.appDir, name );

        Files.createParentDirs( file );
        Files.touch( file );
    }

    @Test
    public void testFindFiles()
        throws Exception
    {
        newFile( "a.txt" );
        newFile( "c/d.txt" );
        newFile( "c/d/e.png" );
        newFile( "c/d/f.png" );
        newFile( "c/d/g.txt" );
        newFile( "c/d/e/a.png" );

        final ResourceKeys keys1 = this.resourceService.findFiles( this.appKey, "c/d", "png", false );
        assertEquals( 2, keys1.getSize() );
        assertEquals( "[myapp:/c/d/e.png, myapp:/c/d/f.png]", keys1.toString() );

        final ResourceKeys keys2 = this.resourceService.findFiles( this.appKey, "c/d", "png", true );
        assertEquals( 3, keys2.getSize() );
        assertEquals( "[myapp:/c/d/e/a.png, myapp:/c/d/e.png, myapp:/c/d/f.png]", keys2.toString() );
    }

    @Test
    public void findFolders()
        throws Exception
    {
        newFile( "a.txt" );
        newFile( "c/d.txt" );
        newFile( "c/d/e.png" );
        newFile( "c/d/f.png" );
        newFile( "c/d/g.txt" );
        newFile( "c/d/e/a.png" );

        final ResourceKeys keys1 = this.resourceService.findFolders( this.appKey, "c" );
        assertEquals( 1, keys1.getSize() );
        assertEquals( "[myapp:/c/d]", keys1.toString() );

        final ResourceKeys keys2 = this.resourceService.findFolders( this.appKey, "c/d" );
        assertEquals( 1, keys2.getSize() );
        assertEquals( "[myapp:/c/d/e]", keys2.toString() );
    }

    @Test
    public void testFindFiles2()
        throws Exception
    {
        newFile( "a.txt" );
        newFile( "b/c.txt" );
        newFile( "c/d/e.png" );

        final ResourceKeys keys1 = this.resourceService.findFiles2( this.appKey, ".+" );
        assertEquals( 3, keys1.getSize() );
        assertEquals( "[myapp:/b/c.txt, myapp:/a.txt, myapp:/c/d/e.png]", keys1.toString() );

        final ResourceKeys keys2 = this.resourceService.findFiles2( this.appKey, "b/c\\.txt" );
        assertEquals( 1, keys2.getSize() );
        assertEquals( "[myapp:/b/c.txt]", keys2.toString() );

        final ResourceKeys keys3 = this.resourceService.findFiles2( this.appKey, ".+\\.txt" );
        assertEquals( 2, keys3.getSize() );
        assertEquals( "[myapp:/b/c.txt, myapp:/a.txt]", keys3.toString() );
    }

    @Test
    public void testFindFolders2()
        throws Exception
    {
        newFile( "a.txt" );
        newFile( "b/c.txt" );
        newFile( "b/d.txt" );
        newFile( "c/d/e.png" );

        final ResourceKeys keys1 = this.resourceService.findFolders2( this.appKey, ".+" );
        assertEquals( 2, keys1.getSize() );
        assertEquals( "[myapp:/b, myapp:/c/d]", keys1.toString() );

        final ResourceKeys keys2 = this.resourceService.findFolders2( this.appKey, "c/.+" );
        assertEquals( 1, keys2.getSize() );
        assertEquals( "[myapp:/c/d]", keys2.toString() );
    }
}
