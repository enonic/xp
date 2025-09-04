package com.enonic.xp.core.impl.service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.service.ServiceDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class YmlServiceDescriptorParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yml = readAsString( "/descriptors/service-descriptor.yml" );

        final ApplicationKey myapp = ApplicationKey.from( "myapp" );

        final ServiceDescriptor.Builder descriptorBuilder = YmlServiceDescriptorParser.parse( yml, myapp );
        descriptorBuilder.key( DescriptorKey.from( myapp, "myservice" ) );

        final ServiceDescriptor descriptor = descriptorBuilder.build();
        assertNotNull( descriptor );
        assertEquals( DescriptorKey.from( myapp, "myservice" ), descriptor.getKey() );

        final PrincipalKeys principalKeys = descriptor.getAllowedPrincipals();
        assertEquals( 2, principalKeys.getSize() );

        final Iterator<PrincipalKey> iterator = principalKeys.iterator();
        assertEquals( "role:system.roleId_1", iterator.next().toString() );
        assertEquals( "role:system.roleId_2", iterator.next().toString() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlServiceDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
