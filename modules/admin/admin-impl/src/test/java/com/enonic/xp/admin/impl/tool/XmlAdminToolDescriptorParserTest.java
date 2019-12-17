package com.enonic.xp.admin.impl.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.xml.parser.XmlModelParserTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XmlAdminToolDescriptorParserTest
    extends XmlModelParserTest
{
    private XmlAdminToolDescriptorParser parser;

    private AdminToolDescriptor.Builder builder;

    @BeforeEach
    public void setup()
    {
        this.parser = new XmlAdminToolDescriptorParser();
        this.parser.currentApplication( ApplicationKey.from( "myapplication" ) );

        this.builder = AdminToolDescriptor.create();
        this.builder.key( DescriptorKey.from( "myapplication:myadmintool" ) );
        this.parser.builder( this.builder );
    }

    @Test
    public void testParse()
        throws Exception
    {
        parse( this.parser, ".xml" );
        assertResult();
    }

    @Test
    public void testParse_noNs()
        throws Exception
    {
        parseRemoveNs( this.parser, ".xml" );
        assertResult();
    }

    private void assertResult()
        throws Exception
    {
        final AdminToolDescriptor result = this.builder.build();
        assertEquals( "myapplication:myadmintool", result.getKey().toString() );
        assertEquals( "My admin tool", result.getDisplayName() );

        assertEquals( "key.display-name", result.getDisplayNameI18nKey() );
        assertEquals( "key.description", result.getDescriptionI18nKey() );

        final PrincipalKeys allowedPrincipals = result.getAllowedPrincipals();
        assertNotNull( allowedPrincipals );
        assertEquals( 1, allowedPrincipals.getSize() );
        assertTrue( allowedPrincipals.first().equals( PrincipalKey.from( "role:system.admin" ) ) );
    }
}
