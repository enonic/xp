package com.enonic.wem.core.module.source;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.io.Files;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.core.config.SystemConfig;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class SourceResolverImplTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private SourceResolver resolver;

    @Before
    public void setup()
        throws Exception
    {
        final File moduleFolder = this.folder.newFolder( "mymodule-1.0.0" );
        final File jsFolder = new File( moduleFolder, "js" );

        assertTrue( jsFolder.mkdir() );

        Files.touch( new File( jsFolder, "my.js" ) );
        Files.touch( new File( jsFolder, "other.js" ) );

        final SystemConfig config = Mockito.mock( SystemConfig.class );
        Mockito.when( config.getModulesDir() ).thenReturn( this.folder.getRoot().toPath() );

        this.resolver = new SourceResolverImpl( config );
    }

    @Test
    public void testResolveAbsolute()
    {
        final ModuleResourceKey key1 = ModuleResourceKey.from( "othermodule-1.0.0:js/my.js" );
        final ModuleSource source1 = this.resolver.resolve( key1 );
        assertEquals( "othermodule-1.0.0:js/my.js", source1.getUri() );
        assertFalse( source1.exists() );

        final ModuleResourceKey key2 = ModuleResourceKey.from( "mymodule-1.0.0:view/test.xsl" );
        final ModuleSource source2 = this.resolver.resolve( key2 );
        assertEquals( "mymodule-1.0.0:view/test.xsl", source2.getUri() );
        assertFalse( source2.exists() );

        final ModuleResourceKey key3 = ModuleResourceKey.from( "mymodule-1.0.0:js/my.js" );
        final ModuleSource source3 = this.resolver.resolve( key3 );
        assertEquals( "mymodule-1.0.0:js/my.js", source3.getUri() );
        assertTrue( source3.exists() );
    }

    @Test
    public void testResolveRelative()
    {
        final ModuleResourceKey base = ModuleResourceKey.from( "mymodule-1.0.0:js/my.js" );

        final ModuleSource source1 = this.resolver.resolve( base, "./other.js" );
        assertEquals( "mymodule-1.0.0:js/other.js", source1.getUri() );
        assertTrue( source1.exists() );

        final ModuleSource source2 = this.resolver.resolve( base, "../js/other.js" );
        assertEquals( "mymodule-1.0.0:js/other.js", source2.getUri() );
        assertTrue( source2.exists() );

        final ModuleSource source3 = this.resolver.resolve( base, "../other.js" );
        assertEquals( "mymodule-1.0.0:other.js", source3.getUri() );
        assertFalse( source3.exists() );
    }

    @Test
    public void testResolveSystem()
    {
        final ModuleResourceKey base = ModuleResourceKey.from( "mymodule-1.0.0:js/my.js" );

        final ModuleSource source1 = this.resolver.resolve( base, "test/classpath.js" );
        assertEquals( "system-0.0.0:test/classpath.js", source1.getUri() );
        assertTrue( source1.exists() );

        final ModuleSource source2 = this.resolver.resolve( source1.getKey(), "./system.js" );
        assertEquals( "system-0.0.0:test/system.js", source2.getUri() );
        assertTrue( source2.exists() );

        final ModuleSource source3 = this.resolver.resolve( source1.getKey(), "../system.js" );
        assertEquals( "system-0.0.0:system.js", source3.getUri() );
        assertFalse( source3.exists() );
    }
}
