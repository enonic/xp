package com.enonic.xp.xml.parser;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.service.ServiceDescriptor;

import static org.junit.Assert.*;

public class XmlServiceDescriptorParserTest
    extends XmlModelParserTest
{
    private XmlServiceDescriptorParser parser;

    private ServiceDescriptor.Builder builder;

    @Before
    public void setup()
    {
        this.parser = new XmlServiceDescriptorParser();
        this.parser.currentApplication( ApplicationKey.from( "myapplication" ) );

        this.builder = ServiceDescriptor.create();
        this.builder.key( DescriptorKey.from( "myapplication:myservice" ) );
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
        final ServiceDescriptor result = this.builder.build();
        assertEquals( "myapplication:myservice", result.getKey().toString() );

        final PrincipalKeys allowedPrincipals = result.getAllowedPrincipals();
        assertNotNull( allowedPrincipals );
        assertEquals( 1, allowedPrincipals.getSize() );
        assertTrue( allowedPrincipals.first().equals( PrincipalKey.from( "role:system.admin" ) ) );
    }
}
