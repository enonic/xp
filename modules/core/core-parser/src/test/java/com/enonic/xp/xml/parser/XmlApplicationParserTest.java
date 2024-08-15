package com.enonic.xp.xml.parser;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.support.XmlTestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XmlApplicationParserTest
{
    private final XmlTestHelper xmlTestHelper;

    private final XmlApplicationParser parser;

    public XmlApplicationParserTest()
    {
        this.xmlTestHelper = new XmlTestHelper( this );
        this.parser = new XmlApplicationParser();
    }

    private String loadTestXml( final String fileName )
    {
        return this.xmlTestHelper.loadTestXml( fileName );
    }

    @Test
    public void testSiteXmlDeserialization()
    {
        final String xml = loadTestXml( "serialized-application.xml" );

        final ApplicationDescriptor.Builder appDescriptorBuilder = ApplicationDescriptor.create();
        ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        this.parser.source( xml ).
            currentApplication( applicationKey ).
            appDescriptorBuilder( appDescriptorBuilder ).
            parse();

        ApplicationDescriptor applicationDescriptor = appDescriptorBuilder.build();

        assertEquals( "My app description", applicationDescriptor.getDescription() );
    }

}
