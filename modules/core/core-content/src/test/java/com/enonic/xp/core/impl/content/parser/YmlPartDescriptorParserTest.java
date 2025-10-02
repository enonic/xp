package com.enonic.xp.core.impl.content.parser;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.region.PartDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YmlPartDescriptorParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/part-descriptor.yml" );

        final ApplicationKey currentApplication = ApplicationKey.from( "myapp" );

        final PartDescriptor.Builder builder = YmlPartDescriptorParser.parse( yaml, currentApplication );

        builder.key( DescriptorKey.from( currentApplication, "my-part" ) );

        final PartDescriptor descriptor = builder.build();

        assertEquals( currentApplication, descriptor.getApplicationKey() );

        assertEquals( "My Part", descriptor.getDisplayName() );
        assertEquals( "Description of My Part", descriptor.getDescription() );
        assertEquals( "part.description.myPart", descriptor.getDescriptionI18nKey() );

        // verify form
        final Form form = descriptor.getConfig();
        assertNotNull( form );
        assertNotNull( form.getFormItem( FormItemPath.from( "myField" ) ) );

        // verify config
        final InputTypeConfig schemaConfig = descriptor.getSchemaConfig();

        assertTrue( schemaConfig.getProperty( "p1" ).isPresent() );
        assertEquals( "v1", schemaConfig.getProperty( "p1" ).get().getValue().asString() );

        assertTrue( schemaConfig.getProperty( "p2" ).isPresent() );
        assertEquals( "v2", schemaConfig.getProperty( "p2" ).get().getValue().asString() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlPageDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
