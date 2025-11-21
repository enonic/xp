package com.enonic.xp.core.impl.idprovider;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.form.Input;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.idprovider.IdProviderDescriptorMode;
import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class YmlIdProviderDescriptorParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yml = readAsString( "/descriptors/idprovider-descriptor.yml" );

        final ApplicationKey myapp = ApplicationKey.from( "myapp" );

        final IdProviderDescriptor.Builder descriptorBuilder = YmlIdProviderDescriptorParser.parse( yml, myapp );
        descriptorBuilder.key( myapp );

        final IdProviderDescriptor descriptor = descriptorBuilder.build();
        assertNotNull( descriptor );
        assertEquals( myapp, descriptor.getKey() );
        assertEquals( IdProviderDescriptorMode.MIXED, descriptor.getMode() );

        final Form config = descriptor.getConfig();

        final Input titleTextLine = config.getFormItem( FormItemPath.from( "title" ) ).toInput();
        assertEquals( InputTypeName.TEXT_LINE, titleTextLine.getInputType() );
        assertEquals( "title", titleTextLine.getName() );
        assertEquals( "Title", titleTextLine.getLabel() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlIdProviderDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
