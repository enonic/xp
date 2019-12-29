package com.enonic.xp.launcher.impl.provision;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BundleInfoFinderTest
{
    @TempDir
    public Path temporaryFolder;

    private BundleInfoFinder finder;

    private Path systemDir;

    @BeforeEach
    public void setup()
        throws Exception
    {
        this.systemDir = Files.createDirectory( this.temporaryFolder.resolve( "system" ) );
        this.finder = new BundleInfoFinder( this.systemDir.toFile() );
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

    private void newFile( final String level, final String name )
        throws Exception
    {
        final Path levelFolder = systemDir.resolve( level );
        Files.createDirectories( levelFolder );
        Files.createFile( levelFolder.resolve( name ) );
    }
}
