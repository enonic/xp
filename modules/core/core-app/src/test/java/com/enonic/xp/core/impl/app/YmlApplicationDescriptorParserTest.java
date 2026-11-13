package com.enonic.xp.core.impl.app;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.util.GenericValue;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class YmlApplicationDescriptorParserTest
{
    private static final ApplicationKey MYAPP = ApplicationKey.from( "myapp" );

    @Test
    void test()
        throws Exception
    {
        final String yml = readAsString( "/descriptors/application-descriptor.yml" );

        final ApplicationDescriptor descriptor = parse( yml );
        assertNotNull( descriptor );
        assertEquals( MYAPP, descriptor.getKey() );
        assertEquals( "Brief description of the application", descriptor.getDescription() );
        assertEquals( "1.2.3", descriptor.getVersion() );
        assertEquals( "8.0.0", descriptor.getMinSystemVersion() );
        assertEquals( "9.0.0", descriptor.getMaxSystemVersion() );

        final GenericValue schemaConfig = descriptor.getSchemaConfig();
        assertEquals( "value_1", schemaConfig.property( "property_1" ).asString() );
        assertEquals( "value_2", schemaConfig.property( "property_2" ).asString() );
    }

    @Test
    void name_matching_application()
    {
        final ApplicationDescriptor descriptor = parse( "kind: \"Application\"\nname: \"myapp\"\ntitle: \"My app\"\n" );
        assertEquals( MYAPP, descriptor.getKey() );
        assertEquals( "My app", descriptor.getTitle() );
    }

    @Test
    void versions_are_optional()
    {
        final ApplicationDescriptor descriptor = parse( "kind: \"Application\"\n" );
        assertNull( descriptor.getVersion() );
        assertNull( descriptor.getMinSystemVersion() );
        assertNull( descriptor.getMaxSystemVersion() );
    }

    @Test
    void versions_must_be_valid()
    {
        for ( final String field : new String[]{"version", "minSystemVersion", "maxSystemVersion"} )
        {
            assertThatThrownBy( () -> parse( "kind: \"Application\"\n" + field + ": \"1.x\"\n" ) )
                .isInstanceOf( Exception.class )
                .hasMessageContaining( "Invalid " + field + " in application descriptor" );
        }
    }

    @Test
    void version_is_declared_as_a_semantic_version_and_held_as_a_bundle_version()
    {
        assertEquals( "1.2.3", parse( "kind: \"Application\"\nversion: \"1.2.3\"\n" ).getVersion() );
        assertEquals( "1.2.3.SNAPSHOT", parse( "kind: \"Application\"\nversion: \"1.2.3-SNAPSHOT\"\n" ).getVersion() );
        assertEquals( "1.2.3.rc_1_build_5", parse( "kind: \"Application\"\nversion: \"1.2.3-rc.1+build.5\"\n" ).getVersion() );

        final ApplicationDescriptor range =
            parse( "kind: \"Application\"\nminSystemVersion: \"8.0.0-SNAPSHOT\"\nmaxSystemVersion: \"9.0.0\"\n" );
        assertEquals( "8.0.0.SNAPSHOT", range.getMinSystemVersion() );
        assertEquals( "9.0.0", range.getMaxSystemVersion() );
    }

    @Test
    void version_written_as_a_bundle_version_is_rejected()
    {
        for ( final String version : new String[]{"1.2.3.SNAPSHOT", "1.2", "1", "01.2.3", "v1.2.3"} )
        {
            assertThatThrownBy( () -> parse( "kind: \"Application\"\nversion: \"" + version + "\"\n" ) )
                .isInstanceOf( Exception.class )
                .hasMessageContaining( "Invalid version in application descriptor: invalid semantic version \"" + version + "\"" );
        }
    }

    @Test
    void name_not_matching_application()
    {
        assertThatThrownBy( () -> parse( "kind: \"Application\"\nname: \"otherapp\"\n" ) )
            .isInstanceOf( Exception.class )
            .hasMessageContaining( "Application name \"otherapp\" in descriptor does not match application \"myapp\"" );
    }

    @Test
    void name_invalid()
    {
        assertThatThrownBy( () -> parse( "kind: \"Application\"\nname: \"my-app\"\n" ) )
            .isInstanceOf( Exception.class )
            .hasMessageContaining( "ApplicationKey is invalid: my-app" );
    }

    @Test
    void type_is_not_supported()
    {
        assertThatThrownBy( () -> parse( "kind: \"Application\"\ntype: \"Static\"\n" ) )
            .isInstanceOf( Exception.class )
            .hasMessageContaining( "\"type\"" );
    }

    private static ApplicationDescriptor parse( final String yml )
    {
        final ApplicationDescriptor.Builder descriptorBuilder = YmlApplicationDescriptorParser.parse( yml, MYAPP );
        descriptorBuilder.key( MYAPP );
        return descriptorBuilder.build();
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlApplicationDescriptorParserTest.class.getResource( name ).toURI() ),
                                 StandardCharsets.UTF_8 );
    }
}