package com.enonic.xp.core.impl.content.parser;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.schema.xdata.MixinDescriptor;
import com.enonic.xp.schema.xdata.MixinName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class YmlXDataParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/mixin-descriptor.yml" );

        final ApplicationKey currentApplication = ApplicationKey.from( "myapp" );

        final MixinDescriptor.Builder builder = YmlMixinDescriptorParser.parse( yaml, currentApplication );

        builder.name( MixinName.from( currentApplication, "my-x-data" ) );

        final MixinDescriptor descriptor = builder.build();

        assertEquals( currentApplication, descriptor.getName().getApplicationKey() );
        assertEquals( "my-x-data", descriptor.getName().getLocalName() );
        assertEquals( "DisplayName of the X-Data", descriptor.getDisplayName() );
        assertNull( descriptor.getDisplayNameI18nKey() );
        assertEquals( "Description of the X-Data", descriptor.getDescription() );
        assertNull( descriptor.getDescriptionI18nKey() );

        // verify form
        final Form form = descriptor.getForm();
        assertNotNull( form );
        assertNotNull( form.getFormItem( FormItemPath.from( "myField" ) ) );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlXDataParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
