package com.enonic.xp.core.impl.schema;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class YmlFormFragmentParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/formfragment-descriptor.yml" );

        final ApplicationKey currentApplication = ApplicationKey.from( "myapp" );

        final FormFragmentDescriptor.Builder builder = YmlFormFragmentParser.parse( yaml, currentApplication );

        builder.name( FormFragmentName.from( currentApplication, "my-form-fragment" ) );

        final FormFragmentDescriptor descriptor = builder.build();

        assertEquals( currentApplication, descriptor.getName().getApplicationKey() );
        assertEquals( "my-form-fragment", descriptor.getName().getLocalName() );
        assertEquals( "DisplayName of the FormFragment", descriptor.getDisplayName() );
        assertNull( descriptor.getDisplayNameI18nKey() );
        assertEquals( "Description of the FormFragment", descriptor.getDescription() );
        assertNull( descriptor.getDescriptionI18nKey() );

        // verify form
        final Form form = descriptor.getForm();
        assertNotNull( form );
        assertNotNull( form.getFormItem( FormItemPath.from( "myField" ) ) );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlFormFragmentParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
