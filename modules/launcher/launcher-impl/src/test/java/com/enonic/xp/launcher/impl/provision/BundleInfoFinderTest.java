package com.enonic.xp.launcher.impl.provision;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Files;

import static org.junit.Assert.*;

public class BundleInfoFinderTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private BundleInfoFinder finder;

    private File systemDir;

    @Before
    public void setup()
        throws Exception
    {
        this.systemDir = this.folder.newFolder( "system" );
        this.finder = new BundleInfoFinder( this.systemDir );
    }

    @Test
    public void findNone()
        throws Exception
    {
        final List<BundleInfo> bundles = this.finder.find();
        assertEquals( 0, bundles.size() );
    }

    @Test
    public void findBundles()
        throws Exception
    {
        newFile( "05", "guava-18.0.jar" );
        newFile( "11", "commons-lang-2.4.jar" );
        newFile( "ab", "other.jar" );
        newFile( "20", "wrong-format.txt" );

        final List<BundleInfo> bundles = this.finder.find();
        assertEquals( 2, bundles.size() );
        assertEquals( "guava-18.0.jar@5", bundles.get( 0 ).toString() );
        assertEquals( "commons-lang-2.4.jar@11", bundles.get( 1 ).toString() );
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void newFile( final String level, final String name )
        throws Exception
    {
        final File levelFolder = new File( this.systemDir, level );
        levelFolder.mkdirs();

        final File file = new File( levelFolder, name );
        Files.touch( file );
    }
}
