package com.enonic.xp.portal.impl.api;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YmlApiDescriptorParserTest
{
    @Test
    void testParseApiDescriptor()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/api-descriptor.yml" );

        final ApplicationKey currentApplication = ApplicationKey.from( "myapp" );

        final ApiDescriptor.Builder builder = YmlApiDescriptorParser.parse( yaml, currentApplication );

        builder.key( DescriptorKey.from( currentApplication, "myapi" ) );

        final ApiDescriptor apiDescriptor = builder.build();

        assertEquals( "GraphQL API", apiDescriptor.getDisplayName() );
        assertEquals( "Description of GraphQL API", apiDescriptor.getDescription() );
        assertEquals( "https://docs.mygraphqlapi.com", apiDescriptor.getDocumentationUrl() );
        assertTrue( apiDescriptor.getMount().contains( "xp" ) );

        final PrincipalKeys principalKeys = apiDescriptor.getAllowedPrincipals();
        assertEquals( 2, principalKeys.getSize() );

        final Iterator<PrincipalKey> iterator = principalKeys.iterator();
        assertEquals( "role:system.roleId_1", iterator.next().toString() );
        assertEquals( "role:system.roleId_2", iterator.next().toString() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlApiDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
