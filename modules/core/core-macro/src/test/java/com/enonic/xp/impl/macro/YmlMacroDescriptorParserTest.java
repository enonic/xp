package com.enonic.xp.impl.macro;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class YmlMacroDescriptorParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/macro-descriptor.yml" );

        final ApplicationKey currentApplication = ApplicationKey.from( "myapp" );

        final MacroDescriptor.Builder builder = YmlMacroDescriptorParser.parse( yaml, currentApplication );

        builder.key( MacroKey.from( currentApplication, "my-macro" ) );

        final MacroDescriptor descriptor = builder.build();

        assertEquals( currentApplication, descriptor.getKey().getApplicationKey() );
        assertEquals( "my-macro", descriptor.getKey().getName() );

        assertEquals( "Twitter", descriptor.getDisplayName() );
        assertEquals( "Insert a single Tweet into your article or website", descriptor.getDescription() );

        // verify form
        final Form form = descriptor.getForm();
        assertNotNull( form );
        assertNotNull( form.getFormItem( FormItemPath.from( "url" ) ) );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlMacroDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
