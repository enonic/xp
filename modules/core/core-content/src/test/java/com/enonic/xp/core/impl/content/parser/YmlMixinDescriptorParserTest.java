package com.enonic.xp.core.impl.content.parser;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.util.GenericValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class YmlMixinDescriptorParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/mixin-descriptor.yml" );

        final ApplicationKey currentApplication = ApplicationKey.from( "myapp" );

        final MixinDescriptor.Builder builder = YmlMixinDescriptorParser.parse( yaml, currentApplication );

        builder.name( MixinName.from( currentApplication, "my-mixin" ) );

        final MixinDescriptor descriptor = builder.build();

        assertEquals( currentApplication, descriptor.getName().getApplicationKey() );
        assertEquals( "my-mixin", descriptor.getName().getLocalName() );
        assertEquals( "DisplayName of the Mixin", descriptor.getTitle() );
        assertNull( descriptor.getTitleI18nKey() );
        assertEquals( "Description of the Mixin", descriptor.getDescription() );
        assertNull( descriptor.getDescriptionI18nKey() );

        // verify form
        final Form form = descriptor.getForm();
        assertNotNull( form );
        assertNotNull( form.getFormItem( FormItemPath.from( "myField" ) ) );

        final GenericValue schemaConfig = descriptor.getSchemaConfig();
        assertEquals( "value_1", schemaConfig.property( "property_1" ).asString() );
        assertEquals( "value_2", schemaConfig.property( "property_2" ).asString() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlMixinDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
