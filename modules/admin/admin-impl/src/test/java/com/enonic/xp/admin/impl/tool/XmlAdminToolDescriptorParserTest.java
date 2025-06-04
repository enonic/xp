package com.enonic.xp.admin.impl.tool;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlModelParserTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        final AdminToolDescriptor toolDescriptor = this.builder.build();
        assertResult( toolDescriptor );
    }

    @Test
    public void testParseWithApis()
        throws Exception
    {
        parse( this.parser, "-apis.xml" );

        final AdminToolDescriptor toolDescriptor = this.builder.build();

        assertResult( toolDescriptor );

        final List<DescriptorKey> apiMounts = toolDescriptor.getApiMounts().stream().toList();
        assertEquals( 2, apiMounts.size() );

        final DescriptorKey apiMountDescriptor1 = apiMounts.get( 0 );
        assertEquals( ApplicationKey.from( "com.enonic.app.myapp" ), apiMountDescriptor1.getApplicationKey() );
        assertEquals( "api-key", apiMountDescriptor1.getName() );

        final DescriptorKey apiMountDescriptor3 = apiMounts.get( 1 );
        assertEquals( ApplicationKey.from( "myapplication" ), apiMountDescriptor3.getApplicationKey() );
        assertEquals( "api-key", apiMountDescriptor3.getName() );
    }

    @Test
    public void testParseWithInterfaces()
        throws Exception
    {
        parse( this.parser, "-interfaces.xml" );

        final AdminToolDescriptor toolDescriptor = this.builder.build();

        assertResult( toolDescriptor );

        assertEquals( 2, toolDescriptor.getInterfaces().size() );

        final Set<String> interfaces = toolDescriptor.getInterfaces();
        assertTrue( interfaces.contains( "admin.dashboard.content-studio" ) );
        assertTrue( interfaces.contains( "admin.dashboard" ) );
    }

    @Test
    public void testParseWithInvalidApis()
    {
        XmlException ex = assertThrows( XmlException.class, () -> parse( this.parser, "-apis-invalid.xml" ) );
        assertEquals( "Invalid applicationKey ''", ex.getMessage() );
    }

    @Test
    public void testParse_noNs()
        throws Exception
    {
        parseRemoveNs( this.parser, ".xml" );

        final AdminToolDescriptor toolDescriptor = this.builder.build();
        assertResult( toolDescriptor );
    }

    private void assertResult( final AdminToolDescriptor toolDescriptor )
    {
        assertEquals( "myapplication:myadmintool", toolDescriptor.getKey().toString() );
        assertEquals( "My admin tool", toolDescriptor.getDisplayName() );

        assertEquals( "key.display-name", toolDescriptor.getDisplayNameI18nKey() );
        assertEquals( "key.description", toolDescriptor.getDescriptionI18nKey() );

        final PrincipalKeys allowedPrincipals = toolDescriptor.getAllowedPrincipals();
        assertNotNull( allowedPrincipals );
        assertEquals( 1, allowedPrincipals.getSize() );
        assertEquals( allowedPrincipals.first(), PrincipalKey.from( "role:system.admin" ) );
    }
}
