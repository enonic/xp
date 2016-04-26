package com.enonic.xp.launcher.impl.provision;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import com.enonic.xp.launcher.impl.config.ConfigProperties;

import static org.junit.Assert.*;

public class BundleInfoLoaderTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private BundleInfoLoader loader;

    private File systemDir;

    @Before
    public void setup()
        throws Exception
    {
        this.systemDir = this.folder.newFolder( "system" );
        final ConfigProperties props = new ConfigProperties();
        this.loader = new BundleInfoLoader( this.systemDir, props );
    }

    @Test
    public void loadNone()
        throws Exception
    {
        final List<BundleInfo> bundles = this.loader.load();
        assertEquals( 0, bundles.size() );
    }

    @Test
    public void loadSingle()
        throws Exception
    {
        writeFile( "bundles.xml", bundle( "com.google.guava:guava:18.0", 20 ), bundle( "commons-lang:commons-lang:2.4", 10 ) );

        final List<BundleInfo> bundles = this.loader.load();
        assertEquals( 2, bundles.size() );
        assertTrue( bundles.get( 0 ).toString().endsWith( "/system/commons-lang/commons-lang/2.4/commons-lang-2.4.jar@10" ) );
        assertTrue( bundles.get( 1 ).toString().endsWith( "/system/com/google/guava/guava/18.0/guava-18.0.jar@20" ) );
    }

    @Test
    public void loadMultiple()
        throws Exception
    {
        writeFile( "1.xml", bundle( "com.google.guava:guava:18.0", 20 ) );
        writeFile( "2.xml", bundle( "commons-lang:commons-lang:2.4", 10 ) );
        writeFile( "3.xml", bundle( "com.google.guava:guava:18.0", 20 ) );

        final List<BundleInfo> bundles = this.loader.load();
        assertEquals( 2, bundles.size() );
        assertTrue( bundles.get( 0 ).toString().endsWith( "/system/commons-lang/commons-lang/2.4/commons-lang-2.4.jar@10" ) );
        assertTrue( bundles.get( 1 ).toString().endsWith( "/system/com/google/guava/guava/18.0/guava-18.0.jar@20" ) );
    }

    private void writeFile( final String name, final BundleInfo... bundles )
        throws Exception
    {
        final StringBuilder str = new StringBuilder( "<bundles>\n" );
        for ( final BundleInfo bundle : bundles )
        {
            str.append( "<bundle level=\"" ).append( bundle.getLevel() ).append( "\">" );
            str.append( bundle.getLocation() ).append( "</bundle>\n" );
        }

        str.append( "</bundles>" );

        final File file = new File( this.systemDir, name );
        Files.write( str.toString(), file, Charsets.UTF_8 );
    }

    private BundleInfo bundle( final String location, final int level )
    {
        return new BundleInfo( location, level );
    }
}
