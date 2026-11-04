package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.support.ResourceTestHelper;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApplicationDescriptorBuilderTest
{
    private static final ApplicationKey APP_KEY = ApplicationKey.from( "myapplication" );

    private static final String APP_DESCRIPTOR_PATH_YML = "application.yml";

    private static final String APP_DESCRIPTOR_PATH_YAML = "application.yaml";

    private static final String ENONIC_APP_DESCRIPTOR_PATH_YAML = "enonic.yaml";

    private static final String APP_ICON_FILENAME = "application.svg";

    private static final String ENONIC_APP_ICON_FILENAME = "enonic.svg";

    private ResourceTestHelper resourceTestHelper;

    @BeforeEach
    void setup()
    {
        resourceTestHelper = new ResourceTestHelper( this );
    }

    @Test
    void buildApplicationDescriptor()
    {
        final ResourceService resourceService = resources( APP_DESCRIPTOR_PATH_YML, APP_ICON_FILENAME );

        final ApplicationDescriptor appDescriptor = build( APP_DESCRIPTOR_PATH_YML, resourceService );

        assertEquals( APP_KEY, appDescriptor.getKey() );
        assertEquals( "My app description", appDescriptor.getDescription() );
        assertNotNull( appDescriptor.getIcon() );
        assertEquals( "image/svg+xml", appDescriptor.getIcon().getMimeType() );
    }

    @Test
    void buildApplicationDescriptorWithYamlExtension()
    {
        final ApplicationDescriptor appDescriptor = build( APP_DESCRIPTOR_PATH_YAML, resources( APP_DESCRIPTOR_PATH_YAML ) );

        assertEquals( "My app description yaml", appDescriptor.getDescription() );
        assertNull( appDescriptor.getIcon() );
    }

    @Test
    void buildApplicationDescriptorEnonicIconTakesPriorityOverApplicationIcon()
        throws Exception
    {
        final ResourceService resourceService = resources( ENONIC_APP_DESCRIPTOR_PATH_YAML, APP_ICON_FILENAME, ENONIC_APP_ICON_FILENAME );

        final ApplicationDescriptor appDescriptor = build( ENONIC_APP_DESCRIPTOR_PATH_YAML, resourceService );

        assertArrayEquals( resourceTestHelper.getTestResource( ENONIC_APP_ICON_FILENAME ).openStream().readAllBytes(),
                           appDescriptor.getIcon().toByteArray() );
    }

    @Test
    void buildApplicationDescriptorWithoutDescriptorFile()
    {
        final ApplicationDescriptor appDescriptor = build( ENONIC_APP_DESCRIPTOR_PATH_YAML, resources() );

        assertEquals( APP_KEY, appDescriptor.getKey() );
        assertEquals( "", appDescriptor.getDescription() );
        assertNull( appDescriptor.getIcon() );
    }

    @Test
    void buildApplicationDescriptorWithoutDescriptorFileKeepsTheIcon()
    {
        final ApplicationDescriptor appDescriptor = build( ENONIC_APP_DESCRIPTOR_PATH_YAML, resources( ENONIC_APP_ICON_FILENAME ) );

        assertEquals( APP_KEY, appDescriptor.getKey() );
        assertEquals( "image/svg+xml", appDescriptor.getIcon().getMimeType() );
    }

    @Test
    void buildApplicationDescriptorFromNodeResource()
    {
        // the descriptor and icon may be served from nodes rather than the bundle: any Resource implementation works
        final Instant timestamp = Instant.parse( "2026-09-11T10:00:00Z" );
        final Map<String, Resource> resources = Map.of( //
            "/enonic.yaml", new NodeValueResource( ResourceKey.from( APP_KEY, "/enonic.yaml" ), ByteSource.wrap(
                "kind: \"Application\"\ndescription: \"From node\"\n".getBytes( StandardCharsets.UTF_8 ) ), timestamp ), //
            "/enonic.svg", new NodeValueResource( ResourceKey.from( APP_KEY, "/enonic.svg" ),
                                                  ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ), timestamp ) );

        final ApplicationDescriptor appDescriptor = build( ENONIC_APP_DESCRIPTOR_PATH_YAML, resourceService( resources ) );

        assertEquals( "From node", appDescriptor.getDescription() );
        assertArrayEquals( "<svg/>".getBytes( StandardCharsets.UTF_8 ), appDescriptor.getIcon().toByteArray() );
        assertEquals( timestamp, appDescriptor.getIcon().getModifiedTime() );
    }

    @Test
    void buildApplicationDescriptorFromNodeResourcePngIcon()
    {
        // the persisted icon is served as enonic.png when it is a png: the mime type follows the path
        final Instant timestamp = Instant.parse( "2026-09-11T10:00:00Z" );
        final Map<String, Resource> resources = Map.of( //
            "/enonic.yaml", new NodeValueResource( ResourceKey.from( APP_KEY, "/enonic.yaml" ),
                                                   ByteSource.wrap( "kind: \"Application\"\n".getBytes( StandardCharsets.UTF_8 ) ),
                                                   timestamp ), "/enonic.png",
            new NodeValueResource( ResourceKey.from( APP_KEY, "/enonic.png" ), ByteSource.wrap( new byte[]{1, 2, 3} ), timestamp ) );

        final ApplicationDescriptor appDescriptor = build( ENONIC_APP_DESCRIPTOR_PATH_YAML, resourceService( resources ) );

        assertArrayEquals( new byte[]{1, 2, 3}, appDescriptor.getIcon().toByteArray() );
        assertEquals( SchemaResourcePaths.PNG_MIME_TYPE, appDescriptor.getIcon().getMimeType() );
        assertEquals( timestamp, appDescriptor.getIcon().getModifiedTime() );
    }

    @Test
    void buildApplicationDescriptorSvgMimeType()
    {
        final ApplicationDescriptor appDescriptor =
            build( ENONIC_APP_DESCRIPTOR_PATH_YAML, resources( ENONIC_APP_DESCRIPTOR_PATH_YAML, ENONIC_APP_ICON_FILENAME ) );

        assertEquals( SchemaResourcePaths.SVG_MIME_TYPE, appDescriptor.getIcon().getMimeType() );
    }

    @Test
    void buildApplicationDescriptorNameMismatch()
    {
        final Map<String, Resource> resources = Map.of( "/enonic.yaml", new NodeValueResource( ResourceKey.from( APP_KEY, "/enonic.yaml" ),
                                                                                               ByteSource.wrap(
                                                                                                   "kind: \"Application\"\nname: \"otherapp\"\n".getBytes(
                                                                                                       StandardCharsets.UTF_8 ) ),
                                                                                               Instant.EPOCH ) );

        assertThatThrownBy( () -> build( ENONIC_APP_DESCRIPTOR_PATH_YAML, resourceService( resources ) ) )
            .isInstanceOf( RuntimeException.class );
    }

    private static ApplicationDescriptor build( final String descriptorFileName, final ResourceService resourceService )
    {
        final Resource descriptor = resourceService.getResource( ResourceKey.from( APP_KEY, "/" + descriptorFileName ) );
        return ApplicationDescriptorBuilder.build( APP_KEY, descriptor, resourceService );
    }

    /**
     * Resource service serving the given test files under their own names in the application root.
     */
    private ResourceService resources( final String... fileNames )
    {
        final Map<String, Resource> resources = new HashMap<>();
        for ( final String fileName : fileNames )
        {
            final URL url = resourceTestHelper.getTestResource( fileName );
            resources.put( "/" + fileName, new UrlResource( ResourceKey.from( APP_KEY, "/" + fileName ), url, "test" ) );
        }
        return resourceService( resources );
    }

    private static ResourceService resourceService( final Map<String, Resource> resources )
    {
        final ResourceService resourceService = mock( ResourceService.class );
        when( resourceService.getResource( any() ) ).thenAnswer( invocation -> {
            final ResourceKey key = invocation.getArgument( 0 );
            final Resource resource = resources.get( key.getPath() );
            return resource != null ? resource : new UrlResource( key, null );
        } );
        return resourceService;
    }
}
