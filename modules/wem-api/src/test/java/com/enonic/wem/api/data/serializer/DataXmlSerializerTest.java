package com.enonic.wem.api.data.serializer;

import java.io.StringReader;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.support.SerializingTestHelper;

public class DataXmlSerializerTest
{
    private final SerializingTestHelper testHelper;

    public DataXmlSerializerTest()
    {
        this.testHelper = new SerializingTestHelper( this, true );
    }

    @Test
    public void test_serialize_parse_roundtrip()
        throws Exception
    {
        final String xml = this.testHelper.loadTextXml( "array.xml" );

        final DataXmlSerializer serializer = new DataXmlSerializer();
        final SAXBuilder parser = new SAXBuilder();
        final Document doc = parser.build( new StringReader( xml ) );

        final RootDataSet dataSet = serializer.parse( doc.getRootElement() );

        final Element result = new Element( "data" );
        serializer.generateRootDataSet( result, dataSet );

        final XMLOutputter outputter = new XMLOutputter( Format.getPrettyFormat() );
        final String resultXml = outputter.outputString( new Document( result ) );

        Assert.assertEquals( xml, resultXml );
    }
}
