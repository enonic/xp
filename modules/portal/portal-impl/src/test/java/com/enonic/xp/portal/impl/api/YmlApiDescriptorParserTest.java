package com.enonic.xp.portal.impl.api;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.util.GenericValue;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

        assertEquals( "GraphQL API", apiDescriptor.getTitle() );
        assertEquals( "Description of GraphQL API", apiDescriptor.getDescription() );
        assertEquals( "https://docs.mygraphqlapi.com", apiDescriptor.getDocumentationUrl() );
        assertTrue( apiDescriptor.getMount().contains( "web" ) );

        final PrincipalKeys principalKeys = apiDescriptor.getAllowedPrincipals();
        assertEquals( 2, principalKeys.getSize() );

        final Iterator<PrincipalKey> iterator = principalKeys.iterator();
        assertEquals( "role:system.roleId_1", iterator.next().toString() );
        assertEquals( "role:system.roleId_2", iterator.next().toString() );

        final GenericValue schemaConfig = apiDescriptor.getSchemaConfig();
        assertEquals( "value_1", schemaConfig.property( "property_1" ).asString() );
        assertEquals( "value_2", schemaConfig.property( "property_2" ).asString() );

        assertEquals( Set.of( "read:content", "myapp:query" ), apiDescriptor.getScopes() );
        assertTrue( apiDescriptor.isScopeSufficient( Set.of( "myapp:query", "openid" ) ) );
        assertFalse( apiDescriptor.isScopeSufficient( Set.of( "myapp:other" ) ) );
        assertFalse( apiDescriptor.isScopeSufficient( Set.of() ), "a token without scopes carries none of them" );
    }

    @Test
    void testScopesAreOptionalAndAreScopeNames()
    {
        final ApplicationKey app = ApplicationKey.from( "myapp" );
        final ApiDescriptor none = YmlApiDescriptorParser.parse( "kind: \"API\"\nallow: [\"role:system.admin\"]\n", app )
            .key( DescriptorKey.from( app, "myapi" ) )
            .build();
        assertTrue( none.getScopes().isEmpty() );
        assertTrue( none.isScopeSufficient( Set.of() ) );

        final ApiDescriptor.Builder spaced = YmlApiDescriptorParser.parse(
            "kind: \"API\"\nallow: [\"role:system.admin\"]\nscopes: [\"read all\"]\n", app ).key( DescriptorKey.from( app, "myapi" ) );
        assertThatThrownBy( spaced::build ).isInstanceOf( IllegalArgumentException.class );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlApiDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
