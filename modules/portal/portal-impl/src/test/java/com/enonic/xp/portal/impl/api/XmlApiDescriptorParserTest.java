package com.enonic.xp.portal.impl.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.xml.parser.XmlModelParserTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XmlApiDescriptorParserTest
    extends XmlModelParserTest
{
    private XmlApiDescriptorParser parser;

    private ApiDescriptor.Builder builder;

    @BeforeEach
    public void setup()
    {
        this.builder = ApiDescriptor.create();
        this.builder.key( DescriptorKey.from( ApplicationKey.from( "myapplication" ), "myapi" ) );

        this.parser = new XmlApiDescriptorParser( this.builder );
        this.parser.currentApplication( ApplicationKey.from( "myapplication" ) );
    }

    @Test
    public void testParse()
        throws Exception
    {
        parse( this.parser, ".xml" );
        assertResult();
    }

    @Test
    public void testParseNoNs()
        throws Exception
    {
        parseRemoveNs( this.parser, ".xml" );
        assertResult();
    }

    private void assertResult()
    {
        final ApiDescriptor result = this.builder.build();
        assertEquals( "myapplication:myapi", result.getKey().toString() );

        assertEquals( "My API", result.getDisplayName() );
        assertEquals( "This is my API", result.getDescription() );
        assertEquals( "https://apis.enonic.com", result.getDocumentationUrl() );
        assertFalse( result.isSlashApi() );

        final PrincipalKeys allowedPrincipals = result.getAllowedPrincipals();
        assertNotNull( allowedPrincipals );
        assertEquals( 1, allowedPrincipals.getSize() );
        assertEquals( allowedPrincipals.first(), PrincipalKey.from( "role:system.admin" ) );

        assertTrue( result.isAccessAllowed( PrincipalKeys.from( "role:system.admin" ) ) );
        assertFalse( result.isAccessAllowed( PrincipalKeys.from( "role:cms.admin" ) ) );
    }
}
