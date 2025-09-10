package com.enonic.xp.core.impl.form.mixin;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class YmlMixinParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/mixin-descriptor.yml" );

        final ApplicationKey currentApplication = ApplicationKey.from( "myapp" );

        final Mixin.Builder builder = YmlMixinParser.parse( yaml, currentApplication );

        builder.name( MixinName.from( currentApplication, "my-mixin" ) );

        final Mixin descriptor = builder.build();

        assertEquals( currentApplication, descriptor.getName().getApplicationKey() );
        assertEquals( "my-mixin", descriptor.getName().getLocalName() );
        assertEquals( "DisplayName of the MixIn", descriptor.getDisplayName() );
        assertNull( descriptor.getDisplayNameI18nKey() );
        assertEquals( "Description of the MixIn", descriptor.getDescription() );
        assertNull( descriptor.getDescriptionI18nKey() );

        // verify form
        final Form form = descriptor.getForm();
        assertNotNull( form );
        assertNotNull( form.getFormItem( FormItemPath.from( "myField" ) ) );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlMixinParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
