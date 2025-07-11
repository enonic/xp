package com.enonic.xp.xml.parser;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.support.XmlTestHelper;
import com.enonic.xp.webapp.WebappDescriptor;
import com.enonic.xp.xml.XmlException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class XmlWebappDescriptorParserTest
{
    private final XmlTestHelper xmlTestHelper;

    private final XmlWebappDescriptorParser parser;

    public XmlWebappDescriptorParserTest()
    {
        this.xmlTestHelper = new XmlTestHelper( this );
        this.parser = new XmlWebappDescriptorParser();
    }

    private String loadTestXml( final String fileName )
    {
        return this.xmlTestHelper.loadTestXml( fileName );
    }

    @Test
    public void testWebappXmlDeserialization()
    {
        final String xml = loadTestXml( "serialized-webapp.xml" );

        final WebappDescriptor.Builder builder = WebappDescriptor.create();
        ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        builder.applicationKey( applicationKey );

        this.parser.source( xml ).currentApplication( applicationKey ).descriptorBuilder( builder ).parse();

        final WebappDescriptor webappDescriptor = builder.build();

        assertEquals( applicationKey, webappDescriptor.getApplicationKey() );
        final List<DescriptorKey> apiMounts = webappDescriptor.getApiMounts().stream().toList();
        assertEquals( 4, apiMounts.size() );

        final DescriptorKey apiMountDescriptor1 = apiMounts.get( 0 );
        assertEquals( ApplicationKey.from( "com.enonic.app.myapp" ), apiMountDescriptor1.getApplicationKey() );
        assertEquals( "api-key1", apiMountDescriptor1.getName() );

        final DescriptorKey apiMountDescriptor2 = apiMounts.get( 1 );
        assertEquals( ApplicationKey.from( "com.enonic.app.myapp" ), apiMountDescriptor2.getApplicationKey() );
        assertEquals( "api-key2", apiMountDescriptor2.getName() );

        final DescriptorKey apiMountDescriptor3 = apiMounts.get( 2 );
        assertEquals( applicationKey, apiMountDescriptor3.getApplicationKey() );
        assertEquals( "api-key", apiMountDescriptor3.getName() );

        final DescriptorKey apiMountDescriptor4 = apiMounts.get( 3 );
        assertEquals( ApplicationKey.from( "com.enonic.app.myapp2" ), apiMountDescriptor4.getApplicationKey() );
        assertEquals( "api-key", apiMountDescriptor4.getName() );
    }

    @Test
    public void testWebappXmlEmpty()
    {
        final String xml = loadTestXml( "empty-webapp.xml" );

        final WebappDescriptor.Builder builder = WebappDescriptor.create();
        ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        builder.applicationKey( applicationKey );

        this.parser.source( xml ).currentApplication( applicationKey ).descriptorBuilder( builder ).parse();

        final WebappDescriptor webappDescriptor = builder.build();

        assertEquals( applicationKey, webappDescriptor.getApplicationKey() );
        assertEquals( 0, webappDescriptor.getApiMounts().getSize() );
    }

    @Test
    public void testWebappXmlInvalid()
    {
        final String xml = loadTestXml( "webapp-invalid-mounts.xml" );

        final WebappDescriptor.Builder builder = WebappDescriptor.create();
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        builder.applicationKey( applicationKey );

        final XmlException ex = assertThrows( XmlException.class, () -> this.parser.source( xml )
            .currentApplication( applicationKey )
            .descriptorBuilder( builder )
            .parse() );

        assertEquals( "Invalid applicationKey ''", ex.getMessage() );
    }
}
