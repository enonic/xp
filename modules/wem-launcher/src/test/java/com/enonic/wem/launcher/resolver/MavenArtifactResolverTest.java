package com.enonic.wem.launcher.resolver;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.enonic.wem.launcher.LauncherException;

import static org.junit.Assert.*;

public class MavenArtifactResolverTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File repoDir;

    private MavenArtifactResolver resolver;

    @Before
    public void setUp()
        throws Exception
    {
        this.repoDir = this.folder.newFolder( "repo" );
        this.resolver = new MavenArtifactResolver( Collections.singletonList( this.repoDir ) );
    }

    private void touchFile( final String name )
        throws Exception
    {
        final File file = new File( this.repoDir, name );
        file.getParentFile().mkdirs();
        new FileOutputStream( file ).close();
    }

    @Test(expected = LauncherException.class)
    public void testNotFound()
    {
        this.resolver.resolve( "group/artifact/1.1.1" );
    }

    @Test(expected = LauncherException.class)
    public void testIllegalUri()
    {
        this.resolver.resolve( "some-other-uri" );
    }

    @Test
    public void testResolve()
        throws Exception
    {
        touchFile( "group/artifact/1.1.1/artifact-1.1.1.jar" );
        final File file = this.resolver.resolve( "group/artifact/1.1.1" );
        assertTrue( file.isFile() );
    }

    @Test
    public void testResolve_ext()
        throws Exception
    {
        touchFile( "group/artifact/1.1.1/artifact-1.1.1.txt" );
        final File file = this.resolver.resolve( "group/artifact/1.1.1/txt" );
        assertTrue( file.isFile() );
    }

    @Test
    public void testResolve_ext_classifier()
        throws Exception
    {
        touchFile( "group/artifact/1.1.1/artifact-1.1.1-all.txt" );
        final File file = this.resolver.resolve( "group/artifact/1.1.1/txt/all" );
        assertTrue( file.isFile() );
    }
}
