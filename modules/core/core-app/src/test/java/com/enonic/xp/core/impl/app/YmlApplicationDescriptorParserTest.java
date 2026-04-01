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

public class YmlApplicationDescriptorParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yml = readAsString( "/descriptors/application-descriptor.yml" );

        final ApplicationKey myapp = ApplicationKey.from( "myapp" );

        final ApplicationDescriptor.Builder descriptorBuilder = YmlApplicationDescriptorParser.parse( yml, myapp );
        descriptorBuilder.key( myapp );

        final ApplicationDescriptor descriptor = descriptorBuilder.build();
        assertNotNull( descriptor );
        assertEquals( myapp, descriptor.getKey() );
        assertEquals( "Brief description of the application", descriptor.getDescription() );

        final GenericValue schemaConfig = descriptor.getSchemaConfig();
        assertEquals( "value_1", schemaConfig.property( "property_1" ).asString() );
        assertEquals( "value_2", schemaConfig.property( "property_2" ).asString() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlApplicationDescriptorParserTest.class.getResource( name ).toURI() ),
                                 StandardCharsets.UTF_8 );
    }
}
