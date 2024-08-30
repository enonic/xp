package com.enonic.xp.admin.impl.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.api.ApiMountDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.xml.XmlException;
import com.enonic.xp.xml.parser.XmlModelParserTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        assertEquals( 4, toolDescriptor.getApiMounts().getSize() );

        final ApiMountDescriptor apiMountDescriptor1 = toolDescriptor.getApiMounts().get( 0 );
        assertEquals( ApplicationKey.from( "com.enonic.app.myapp" ), apiMountDescriptor1.getApplicationKey() );
        assertEquals( "api-key", apiMountDescriptor1.getApiKey() );

        final ApiMountDescriptor apiMountDescriptor2 = toolDescriptor.getApiMounts().get( 1 );
        assertEquals( ApplicationKey.from( "com.enonic.app.myapp" ), apiMountDescriptor2.getApplicationKey() );
        assertEquals( "", apiMountDescriptor2.getApiKey() );

        final ApiMountDescriptor apiMountDescriptor3 = toolDescriptor.getApiMounts().get( 2 );
        assertEquals( ApplicationKey.from( "myapplication" ), apiMountDescriptor3.getApplicationKey() );
        assertEquals( "api-key", apiMountDescriptor3.getApiKey() );

        final ApiMountDescriptor apiMountDescriptor4 = toolDescriptor.getApiMounts().get( 3 );
        assertEquals( ApplicationKey.from( "myapplication" ), apiMountDescriptor4.getApplicationKey() );
        assertEquals( "", apiMountDescriptor4.getApiKey() );
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
