package com.enonic.xp.core.impl.app;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationType;
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
        assertEquals( ApplicationType.BUNDLE, descriptor.getType() );

        final GenericValue schemaConfig = descriptor.getSchemaConfig();
        assertEquals( "value_1", schemaConfig.property( "property_1" ).asString() );
        assertEquals( "value_2", schemaConfig.property( "property_2" ).asString() );
    }

    @Test
    void type_static()
    {
        final ApplicationDescriptor descriptor = parse( "kind: \"Application\"\ntype: \"Static\"\n" );
        assertEquals( ApplicationType.STATIC, descriptor.getType() );
    }

    @Test
    void type_bundle()
    {
        final ApplicationDescriptor descriptor = parse( "kind: \"Application\"\ntype: \"Bundle\"\n" );
        assertEquals( ApplicationType.BUNDLE, descriptor.getType() );
    }

    @Test
    void type_unknown()
    {
        final Exception ex = assertThrows( Exception.class, () -> parse( "kind: \"Application\"\ntype: \"Virtual\"\n" ) );
        assertTrue( ex.getMessage().contains( "Unknown application type \"Virtual\"" ), ex.getMessage() );
    }

    @Test
    void type_case_sensitive()
    {
        final Exception ex = assertThrows( Exception.class, () -> parse( "kind: \"Application\"\ntype: \"static\"\n" ) );
        assertTrue( ex.getMessage().contains( "Unknown application type \"static\"" ), ex.getMessage() );
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
