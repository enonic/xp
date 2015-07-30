package com.enonic.xp.core.impl.site;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.support.SerializingTestHelper;

public class XmlSiteParserTest
{
    private final SerializingTestHelper serializingTestHelper;

    private final XmlSiteParser parser;

    public XmlSiteParserTest()
    {
        this.serializingTestHelper = new SerializingTestHelper( this, true );
        this.parser = new XmlSiteParser();
    }

    private String loadTestXml( final String fileName )
    {
        return this.serializingTestHelper.loadTextXml( fileName );
    }

    @Test
    public void testSiteXmlDeserialization()
    {
        final String xml = loadTestXml( "serialized-site.xml" );

        final SiteDescriptor.Builder siteDescriptorBuilder = SiteDescriptor.create();
        ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        this.parser.source( xml ).
            applicationKey( applicationKey ).
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
            applicationKey( applicationKey ).
            siteDescriptorBuilder( siteDescriptorBuilder ).
            parse();

        SiteDescriptor siteDescriptor = siteDescriptorBuilder.build();

        Assert.assertEquals( 0, siteDescriptor.getForm().getFormItems().size() );
        Assert.assertEquals( 0, siteDescriptor.getMetaSteps().getSize() );
    }
}
