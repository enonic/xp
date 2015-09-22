package com.enonic.xp.xml.parser;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.support.XmlTestHelper;

public class XmlSiteParserTest
{
    private final XmlTestHelper xmlTestHelper;

    private final XmlSiteParser parser;

    public XmlSiteParserTest()
    {
        this.xmlTestHelper = new XmlTestHelper( this );
        this.parser = new XmlSiteParser();
    }

    private String loadTestXml( final String fileName )
    {
        return this.xmlTestHelper.loadTestXml( fileName );
    }

    @Test
    public void testSiteXmlDeserialization()
    {
        final String xml = loadTestXml( "serialized-site.xml" );

        final SiteDescriptor.Builder siteDescriptorBuilder = SiteDescriptor.create();
        ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        this.parser.source( xml ).
            currentApplication( applicationKey ).
            siteDescriptorBuilder( siteDescriptorBuilder ).
            parse();

        SiteDescriptor siteDescriptor = siteDescriptorBuilder.build();

        Assert.assertEquals( 1, siteDescriptor.getForm().getFormItems().size() );
        Assert.assertEquals( 2, siteDescriptor.getMetaSteps().getSize() );
    }

    @Test
    public void testEmptySiteXmlDeserialization()
    {
        final String xml = loadTestXml( "empty-site.xml" );

        final SiteDescriptor.Builder siteDescriptorBuilder = SiteDescriptor.create();
        ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        this.parser.source( xml ).
            currentApplication( applicationKey ).
            siteDescriptorBuilder( siteDescriptorBuilder ).
            parse();

        SiteDescriptor siteDescriptor = siteDescriptorBuilder.build();

        Assert.assertEquals( 0, siteDescriptor.getForm().getFormItems().size() );
        Assert.assertEquals( 0, siteDescriptor.getMetaSteps().getSize() );
    }
}
