package com.enonic.xp.admin.impl.tool;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.security.PrincipalKeys;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class YmlAdminToolDescriptorParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yml = readAsString( "/descriptors/admintool-descriptor.yml" );

        final ApplicationKey myapp = ApplicationKey.from( "myapp" );

        final AdminToolDescriptor.Builder descriptorBuilder = YmlAdminToolDescriptorParser.parse( yml, myapp );
        descriptorBuilder.key( DescriptorKey.from( myapp, "mytool" ) );

        final AdminToolDescriptor descriptor = descriptorBuilder.build();
        assertNotNull( descriptor );
        assertEquals( DescriptorKey.from( myapp, "mytool" ), descriptor.getKey() );
        assertEquals( "Content Studio", descriptor.getDisplayName() );
        assertEquals( "Manage content and sites", descriptor.getDescription() );

        final PrincipalKeys allowedPrincipals = descriptor.getAllowedPrincipals();
        assertEquals( 1, allowedPrincipals.getSize() );
        assertEquals( "role:system.authenticated", allowedPrincipals.first().toString() );

        final DescriptorKeys apiMounts = descriptor.getApiMounts();
        assertEquals( 2, apiMounts.getSize() );
        final Iterator<DescriptorKey> apisIterator = apiMounts.iterator();
        assertEquals( "admin:widget", apisIterator.next().toString() );
        assertEquals( "myapp:content", apisIterator.next().toString() );

        final Iterator<String> interfacesIterator = descriptor.getInterfaces().iterator();
        assertEquals( "contentstudio.menuitem", interfacesIterator.next() );
        assertEquals( "contentstudio.contextpanel", interfacesIterator.next() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlAdminToolDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
