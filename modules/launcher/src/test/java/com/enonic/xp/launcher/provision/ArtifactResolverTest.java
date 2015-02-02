package com.enonic.xp.launcher.provision;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Files;

import junit.framework.Assert;

public class ArtifactResolverTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File repoOne;

    private File repoTwo;

    private ArtifactResolver resolver;

    @Before
    public void setup()
    {
        this.repoOne = this.temporaryFolder.newFolder( "repo1" );
        this.repoTwo = this.temporaryFolder.newFolder( "repo2" );

        this.resolver = new ArtifactResolver();
        this.resolver.addRepo( this.repoOne );
        this.resolver.addRepo( this.repoTwo );
    }

    private File touchFile( final File baseDir, final String path )
        throws Exception
    {
        final File file = new File( baseDir, path );
        final File parentFile = file.getParentFile();
        if ( !parentFile.exists() )
        {
            Assert.assertTrue( parentFile.mkdirs() );
        }

        Files.touch( file );
        return file;
    }

    @Test
    public void testNotValidUri()
    {
        final String resolved = this.resolver.resolve( "not.valid" );
        Assert.assertNull( resolved );
    }

    @Test
    public void testNotFound()
    {
        final String resolved = this.resolver.resolve( "com.enonic.test/bundle1/2.3.1" );
        Assert.assertNull( resolved );
    }

    @Test
    public void testFoundRepo1()
        throws Exception
    {
        final File file1 = touchFile( this.repoOne, "com/enonic/test/bundle1/2.3.1/bundle1-2.3.1.jar" );
        final String resolved1 = this.resolver.resolve( "com.enonic.test/bundle1/2.3.1" );
        Assert.assertEquals( file1.toURI().toString(), resolved1 );

        final File file2 = touchFile( this.repoOne, "com/enonic/test/bundle1/2.3.1/bundle1-2.3.1.txt" );
        final String resolved2 = this.resolver.resolve( "com.enonic.test/bundle1/2.3.1/txt" );
        Assert.assertEquals( file2.toURI().toString(), resolved2 );

        final File file3 = touchFile( this.repoOne, "com/enonic/test/bundle1/2.3.1/bundle1-2.3.1-other.txt" );
        final String resolved3 = this.resolver.resolve( "com.enonic.test/bundle1/2.3.1/txt/other" );
        Assert.assertEquals( file3.toURI().toString(), resolved3 );
    }

    @Test
    public void testFoundRepo2()
        throws Exception
    {
        final File file1 = touchFile( this.repoTwo, "com/enonic/test/bundle1/2.3.1/bundle1-2.3.1.jar" );
        final String resolved1 = this.resolver.resolve( "com.enonic.test/bundle1/2.3.1" );
        Assert.assertEquals( file1.toURI().toString(), resolved1 );

        final File file2 = touchFile( this.repoTwo, "com/enonic/test/bundle1/2.3.1/bundle1-2.3.1.txt" );
        final String resolved2 = this.resolver.resolve( "com.enonic.test/bundle1/2.3.1/txt" );
        Assert.assertEquals( file2.toURI().toString(), resolved2 );

        final File file3 = touchFile( this.repoTwo, "com/enonic/test/bundle1/2.3.1/bundle1-2.3.1-other.txt" );
        final String resolved3 = this.resolver.resolve( "com.enonic.test/bundle1/2.3.1/txt/other" );
        Assert.assertEquals( file3.toURI().toString(), resolved3 );
    }
}
