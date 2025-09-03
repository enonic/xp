package com.enonic.xp.admin.impl.widget;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.impl.tool.YmlAdminToolDescriptorParserTest;
import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKeys;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class YmlWidgetDescriptorParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yml = readAsString( "/descriptors/widget-descriptor.yml" );

        final ApplicationKey myapp = ApplicationKey.from( "myapp" );

        final WidgetDescriptor.Builder descriptorBuilder = YmlWidgetDescriptorParser.parse( yml );
        descriptorBuilder.key( DescriptorKey.from( myapp, "mywidget" ) );

        final WidgetDescriptor descriptor = descriptorBuilder.build();
        assertNotNull( descriptor );
        assertEquals( DescriptorKey.from( myapp, "mywidget" ), descriptor.getKey() );
        assertEquals( "My widget", descriptor.getDisplayName() );
        assertEquals( "i18n.my-widget.display-name", descriptor.getDisplayNameI18nKey() );
        assertEquals( "My widget description", descriptor.getDescription() );

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
