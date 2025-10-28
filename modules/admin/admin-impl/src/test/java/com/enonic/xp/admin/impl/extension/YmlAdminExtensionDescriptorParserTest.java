package com.enonic.xp.admin.impl.extension;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.impl.tool.YmlAdminToolDescriptorParserTest;
import com.enonic.xp.admin.extension.AdminExtensionDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKeys;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class YmlAdminExtensionDescriptorParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yml = readAsString( "/descriptors/adminextension-descriptor.yml" );

        final ApplicationKey myapp = ApplicationKey.from( "myapp" );

        final AdminExtensionDescriptor.Builder descriptorBuilder = YmlAdminExtensionDescriptorParser.parse( yml, myapp );
        descriptorBuilder.key( DescriptorKey.from( myapp, "myextension" ) );

        final AdminExtensionDescriptor descriptor = descriptorBuilder.build();
        assertNotNull( descriptor );
        assertEquals( DescriptorKey.from( myapp, "myextension" ), descriptor.getKey() );
        assertEquals( "My extension", descriptor.getDisplayName() );
        assertEquals( "i18n.my-extension.display-name", descriptor.getDisplayNameI18nKey() );
        assertEquals( "My extension description", descriptor.getDescription() );

        final PrincipalKeys allowedPrincipals = descriptor.getAllowedPrincipals();
        assertEquals( 1, allowedPrincipals.getSize() );
        assertEquals( "role:system.authenticated", allowedPrincipals.first().toString() );

        final Iterator<String> interfacesIterator = descriptor.getInterfaces().iterator();
        assertEquals( "interface_1", interfacesIterator.next() );
        assertEquals( "interface_2", interfacesIterator.next() );

        final Map<String, String> config = descriptor.getConfig();

        assertEquals( 2, config.size() );
        assertEquals( "value_1", config.get( "property_1" ) );
        assertEquals( "value_2", config.get( "property_2" ) );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlAdminToolDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
