package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.support.ResourceTestHelper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationDescriptorBuilderTest
{
    private static final ApplicationKey APP_KEY = ApplicationKey.from( "myapplication" );

    private static final String APP_DESCRIPTOR_PATH_YML = "application.yml";

    private static final String APP_DESCRIPTOR_PATH_YAML = "application.yaml";

    private static final String ENONIC_APP_DESCRIPTOR_PATH_YML = "enonic.yml";

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
        final ApplicationDescriptor appDescriptor =
            ApplicationDescriptorBuilder.build( APP_KEY, resolver( APP_DESCRIPTOR_PATH_YML, APP_ICON_FILENAME ) );

        assertEquals( APP_KEY, appDescriptor.getKey() );
        assertEquals( "My app description", appDescriptor.getDescription() );
        assertNotNull( appDescriptor.getIcon() );
        assertEquals( "image/svg+xml", appDescriptor.getIcon().getMimeType() );
    }

    @Test
    void buildApplicationDescriptorWithYamlExtension()
    {
        final ApplicationDescriptor appDescriptor = ApplicationDescriptorBuilder.build( APP_KEY, resolver( APP_DESCRIPTOR_PATH_YAML ) );

        assertEquals( "My app description yaml", appDescriptor.getDescription() );
        assertNull( appDescriptor.getIcon() );
    }

    @Test
    void buildApplicationDescriptorYamlTakesPriorityOverYml()
    {
        final ApplicationDescriptor appDescriptor =
            ApplicationDescriptorBuilder.build( APP_KEY, resolver( APP_DESCRIPTOR_PATH_YML, APP_DESCRIPTOR_PATH_YAML ) );

        assertEquals( "My app description yaml", appDescriptor.getDescription() );
    }

    @Test
    void buildApplicationDescriptorWithEnonicYmlExtension()
    {
        final ApplicationDescriptor appDescriptor =
            ApplicationDescriptorBuilder.build( APP_KEY, resolver( ENONIC_APP_DESCRIPTOR_PATH_YML ) );

        assertEquals( "My app description", appDescriptor.getDescription() );
    }

    @Test
    void buildApplicationDescriptorWithEnonicYamlExtension()
    {
        final ApplicationDescriptor appDescriptor =
            ApplicationDescriptorBuilder.build( APP_KEY, resolver( ENONIC_APP_DESCRIPTOR_PATH_YAML ) );

        assertEquals( "My app description YAML", appDescriptor.getDescription() );
    }

    @Test
    void buildApplicationDescriptorEnonicYamlTakesPriorityOverEnonicYml()
    {
        final ApplicationDescriptor appDescriptor = ApplicationDescriptorBuilder.build( APP_KEY, resolver( ENONIC_APP_DESCRIPTOR_PATH_YML,
                                                                                                           ENONIC_APP_DESCRIPTOR_PATH_YAML ) );

        assertEquals( "My app description YAML", appDescriptor.getDescription() );
    }

    @Test
    void buildApplicationDescriptorEnonicTakesPriorityOverApplication()
    {
        final ApplicationDescriptor appDescriptor = ApplicationDescriptorBuilder.build( APP_KEY, resolver( APP_DESCRIPTOR_PATH_YAML,
                                                                                                           ENONIC_APP_DESCRIPTOR_PATH_YAML ) );

        assertEquals( "My app description YAML", appDescriptor.getDescription() );
    }

    @Test
    void buildApplicationDescriptorEnonicIconTakesPriorityOverApplicationIcon()
        throws Exception
    {
        final ApplicationDescriptor appDescriptor =
            ApplicationDescriptorBuilder.build( APP_KEY, resolver( ENONIC_APP_DESCRIPTOR_PATH_YAML, APP_ICON_FILENAME, ENONIC_APP_ICON_FILENAME ) );

        assertArrayEquals( resourceTestHelper.getTestResource( ENONIC_APP_ICON_FILENAME ).openStream().readAllBytes(),
                           appDescriptor.getIcon().toByteArray() );
    }

    @Test
    void buildApplicationDescriptorWithoutDescriptorFile()
    {
        final ApplicationDescriptor appDescriptor = ApplicationDescriptorBuilder.build( APP_KEY, resolver() );

        assertEquals( APP_KEY, appDescriptor.getKey() );
        assertEquals( "", appDescriptor.getDescription() );
        assertNull( appDescriptor.getIcon() );
    }

    @Test
    void buildApplicationDescriptorFromNodeResource()
    {
        // the descriptor and icon may be served from nodes rather than the bundle: any Resource implementation works
        final Instant timestamp = Instant.parse( "2026-09-11T10:00:00Z" );
        final Map<String, Resource> resources = Map.of( //
            "/enonic.yaml", new NodeValueResource( ResourceKey.from( APP_KEY, "/enonic.yaml" ), ByteSource.wrap(
                "kind: \"Application\"\ndescription: \"From node\"\n".getBytes( StandardCharsets.UTF_8 ) ), timestamp ), //
            "/enonic.svg",
            new NodeValueResource( ResourceKey.from( APP_KEY, "/enonic.svg" ), ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ),
                                   timestamp ) );

        final ApplicationDescriptor appDescriptor = ApplicationDescriptorBuilder.build( APP_KEY, resolver( resources ) );

        assertEquals( "From node", appDescriptor.getDescription() );
        assertArrayEquals( "<svg/>".getBytes( StandardCharsets.UTF_8 ), appDescriptor.getIcon().toByteArray() );
        assertEquals( timestamp, appDescriptor.getIcon().getModifiedTime() );
    }

    @Test
    void buildApplicationDescriptorNameMismatch()
    {
        final Map<String, Resource> resources = Map.of( "/enonic.yaml", new NodeValueResource( ResourceKey.from( APP_KEY, "/enonic.yaml" ),
                                                                                                ByteSource.wrap(
                                                                                                    "kind: \"Application\"\nname: \"otherapp\"\n".getBytes(
                                                                                                        StandardCharsets.UTF_8 ) ),
                                                                                                Instant.EPOCH ) );

        assertThrows( RuntimeException.class, () -> ApplicationDescriptorBuilder.build( APP_KEY, resolver( resources ) ) );
    }

    /**
     * Resolver serving the given test files under their own names in the application root.
     */
    private ApplicationUrlResolver resolver( final String... fileNames )
    {
        final Map<String, Resource> resources = new java.util.HashMap<>();
        for ( final String fileName : fileNames )
        {
            final URL url = resourceTestHelper.getTestResource( fileName );
            resources.put( "/" + fileName, new UrlResource( ResourceKey.from( APP_KEY, "/" + fileName ), url, "test" ) );
        }
        return resolver( resources );
    }

    private static ApplicationUrlResolver resolver( final Map<String, Resource> resources )
    {
        return new ApplicationUrlResolver()
        {
            @Override
            public Set<String> findFiles()
            {
                return resources.keySet();
            }

            @Override
            public Resource findResource( final String path )
            {
                return resources.get( path );
            }
        };
    }
}