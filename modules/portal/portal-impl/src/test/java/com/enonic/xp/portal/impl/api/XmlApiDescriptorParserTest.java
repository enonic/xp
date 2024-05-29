package com.enonic.xp.portal.impl.api;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.api.ApiContextPath;
import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.api.ApiMount;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.xml.parser.XmlModelParserTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    public void testParseWithoutMountsNode()
    {
        Exception ex = assertThrows( Exception.class, () -> parse( this.parser, "_WithoutMountsNode.xml" ) );
        assertTrue( ex.getMessage()
                        .contains(
                            "The content of element 'api' is not complete. One of '{\"urn:enonic:xp:model:1.0\":mounts}' is expected" ) );
    }

    @Test
    public void testParseWithoutContextPathNode()
        throws Exception
    {
        parse( this.parser, "_WithoutContextPath.xml" );

        final ApiDescriptor result = this.builder.build();

        assertEquals( ApiContextPath.DEFAULT, result.getContextPath() );
    }

    @Test
    public void testParse_WithoutAllowNode()
        throws Exception
    {
        parse( this.parser, "_WithoutAllowNode.xml" );

        final ApiDescriptor result = this.builder.build();

        assertNull( result.getAllowedPrincipals() );
    }

    private void assertResult()
    {
        final ApiDescriptor result = this.builder.build();
        assertEquals( "myapplication:myapi", result.key().toString() );

        final PrincipalKeys allowedPrincipals = result.getAllowedPrincipals();
        assertNotNull( allowedPrincipals );
        assertEquals( 1, allowedPrincipals.getSize() );
        assertEquals( allowedPrincipals.first(), PrincipalKey.from( "role:system.admin" ) );

        final Set<ApiMount> apiMounts = result.getMounts();
        assertEquals( 3, apiMounts.size() );
        assertTrue( apiMounts.contains( ApiMount.API ) );
        assertTrue( apiMounts.contains( ApiMount.ALL_SITES ) );
        assertTrue( apiMounts.contains( ApiMount.SITE ) );

        assertTrue( result.isAccessAllowed( PrincipalKeys.from( "role:system.admin" ) ) );
        assertFalse( result.isAccessAllowed( PrincipalKeys.from( "role:cms.admin" ) ) );

        assertEquals( ApiContextPath.ANY, result.getContextPath() );
    }
}
