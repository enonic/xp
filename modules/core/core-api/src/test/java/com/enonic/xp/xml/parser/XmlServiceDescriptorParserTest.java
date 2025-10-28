package com.enonic.xp.xml.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.service.ServiceDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlServiceDescriptorParserTest
    extends XmlModelParserTest
{
    private XmlServiceDescriptorParser parser;

    private ServiceDescriptor.Builder builder;

    @BeforeEach
    void setup()
    {
        this.parser = new XmlServiceDescriptorParser();
        this.parser.currentApplication( ApplicationKey.from( "myapplication" ) );

        this.builder = ServiceDescriptor.create();
        this.builder.key( DescriptorKey.from( "myapplication:myservice" ) );
        this.parser.builder( this.builder );
    }

    @Test
    void testParse()
        throws Exception
    {
        parse( this.parser, ".xml" );
        assertResult();
    }

    @Test
    void testParse_noNs()
        throws Exception
    {
        parseRemoveNs( this.parser, ".xml" );
        assertResult();
    }

    private void assertResult()
    {
        final ServiceDescriptor result = this.builder.build();
        assertEquals( "myapplication:myservice", result.getKey().toString() );

        final PrincipalKeys allowedPrincipals = result.getAllowedPrincipals();
        assertNotNull( allowedPrincipals );
        assertEquals( 1, allowedPrincipals.getSize() );
        assertTrue( allowedPrincipals.first().equals( PrincipalKey.from( "role:system.admin" ) ) );
    }
}
