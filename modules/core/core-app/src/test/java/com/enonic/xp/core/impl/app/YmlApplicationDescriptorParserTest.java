package com.enonic.xp.core.impl.app;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.util.GenericValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void name_not_matching_application()
    {
        final Exception ex = assertThrows( Exception.class, () -> parse( "kind: \"Application\"\nname: \"otherapp\"\n" ) );
        assertTrue( ex.getMessage().contains( "Application name \"otherapp\" in descriptor does not match application \"myapp\"" ),
                    ex.getMessage() );
    }

    @Test
    void name_invalid()
    {
        final Exception ex = assertThrows( Exception.class, () -> parse( "kind: \"Application\"\nname: \"my-app\"\n" ) );
        assertTrue( ex.getMessage().contains( "ApplicationKey is invalid: my-app" ), ex.getMessage() );
    }

    @Test
    void type_is_not_supported()
    {
        final Exception ex = assertThrows( Exception.class, () -> parse( "kind: \"Application\"\ntype: \"Static\"\n" ) );
        assertTrue( ex.getMessage().contains( "\"type\"" ), ex.getMessage() );
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