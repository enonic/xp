package com.enonic.xp.admin.impl.adminapp;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.admin.adminapp.AdminApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.xml.parser.XmlModelParserTest;

import static org.junit.Assert.*;

public class XmlAdminApplicationDescriptorParserTest
    extends XmlModelParserTest
{
    private XmlAdminApplicationDescriptorParser parser;

    private AdminApplicationDescriptor.Builder builder;

    @Before
    public void setup()
    {
        this.parser = new XmlAdminApplicationDescriptorParser();
        this.parser.currentApplication( ApplicationKey.from( "myapplication" ) );

        this.builder = AdminApplicationDescriptor.create();
        this.builder.key( DescriptorKey.from( "myapplication:myadminapp" ) );
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
        final AdminApplicationDescriptor result = this.builder.build();
        assertEquals( "myapplication:myadminapp", result.getKey().toString() );
        assertEquals( "My admin app", result.getDisplayName() );
        assertEquals( "/path/to/icon", result.getIcon() );

        final PrincipalKeys allowedPrincipals = result.getAllowedPrincipals();
        assertNotNull( allowedPrincipals );
        assertEquals( 1, allowedPrincipals.getSize() );
        assertTrue( allowedPrincipals.first().equals( PrincipalKey.from( "role:system.admin" ) ) );
    }
}
