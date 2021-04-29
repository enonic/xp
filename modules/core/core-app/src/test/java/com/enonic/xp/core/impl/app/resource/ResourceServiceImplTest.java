package com.enonic.xp.core.impl.app.resource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationFactoryService;
import com.enonic.xp.core.impl.app.MockApplication;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceProcessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ResourceServiceImplTest
{
    Path temporaryFolder;

    ApplicationKey appKey;

    ResourceServiceImpl resourceService;

    Path appDir;

    @BeforeEach
    void setup()
        throws Exception
    {
        //TODO @TempDir JUnit5 suits better, but tests fail due to https://bugs.openjdk.java.net/browse/JDK-6956385
        temporaryFolder = Files.createTempDirectory( "resourceServiceImplTest" );

        this.appDir = Files.createDirectory( this.temporaryFolder.resolve( "myapp" ) );

        this.appKey = ApplicationKey.from( "myapp" );
        final ApplicationFactoryService applicationFactoryService = Mockito.mock( ApplicationFactoryService.class );

        final MockApplication app = new MockApplication();
        app.setStarted( true );
        app.setResourcePath( this.appDir );

        when( applicationFactoryService.findActiveApplication( this.appKey ) ).thenReturn( Optional.of( app ) );

        this.resourceService = new ResourceServiceImpl( applicationFactoryService );
    }

    private void newFile( final String name )
        throws Exception
    {
        final Path file = appDir.resolve( name );
        Files.createDirectories( file.getParent() );
        Files.createFile( file );
    }

    @Test
    void testFindFiles()
        throws Exception
    {
        newFile( "a.txt" );
        newFile( "b/c.txt" );
        newFile( "c/d/e.png" );

        final ResourceKeys keys1 = this.resourceService.findFiles( this.appKey, ".+" );
        assertEquals( 3, keys1.getSize() );
        assertEquals( "[myapp:/b/c.txt, myapp:/a.txt, myapp:/c/d/e.png]", keys1.toString() );

        final ResourceKeys keys2 = this.resourceService.findFiles( this.appKey, "b/c\\.txt" );
        assertEquals( 1, keys2.getSize() );
        assertEquals( "[myapp:/b/c.txt]", keys2.toString() );

        final ResourceKeys keys3 = this.resourceService.findFiles( this.appKey, ".+\\.txt" );
        assertEquals( 2, keys3.getSize() );
        assertEquals( "[myapp:/b/c.txt, myapp:/a.txt]", keys3.toString() );
    }

    private String processResource( final String segment, final String key, final String suffix )
    {
        final ResourceProcessor.Builder<String, String> processor = new ResourceProcessor.Builder<>();
        processor.key( key );
        processor.keyTranslator( name -> ResourceKey.from( "myapp:/" + name ) );
        processor.segment( segment );
        processor.processor( res -> res.getKey().toString() + "->" + suffix );

        return this.resourceService.processResource( processor.build() );
    }

    @Test
    public void testProcessResource()
        throws Exception
    {
        newFile( "a.txt" );

        final String value1 = processResource( "segment1", "a.txt", "1" );
        assertEquals( "myapp:/a.txt->1", value1 );

        final String value2 = processResource( "segment1", "a.txt", "2" );
        assertEquals( value1, value2 );

        this.resourceService.invalidate( ApplicationKey.from( "myapp" ) );

        final String value3 = processResource( "segment1", "a.txt", "3" );
        assertEquals( "myapp:/a.txt->3", value3 );

        final String value4 = processResource( "segment1", "a.txt", "4" );
        assertEquals( value3, value4 );

        final String value5 = processResource( "segment2", "a.txt", "5" );
        assertEquals( "myapp:/a.txt->5", value5 );
    }

    @Test
    void testProcessResource_notFound()
    {
        final String value = processResource( "segment1", "a.txt", "1" );
        assertNull( value );
    }

    @Test
    void resourceHash()
    {
        assertThat( this.resourceService.resourceHash( ResourceKey.assets( appKey ) ) ).isNotEmpty();
    }

    @Test
    void resourceHash_unsupported()
    {
        // API is only open to acept limited set of resources that we know we need hash for.
        assertThrows( IllegalArgumentException.class, () -> this.resourceService.resourceHash( ResourceKey.from( appKey, "/" ) ) );
    }
}
