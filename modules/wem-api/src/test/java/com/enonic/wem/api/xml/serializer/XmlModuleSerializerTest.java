package com.enonic.wem.api.xml.serializer;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.api.xml.model.XmlModule;

public class XmlModuleSerializerTest
    extends BaseXmlSerializer2Test
{
    @Test
    public void test_parse_serialize()
        throws Exception
    {
        final String xml = readFromFile( "module.xml" );

        final XmlModule xmlObject = XmlSerializers2.module().parse( xml );
        Assert.assertNotNull( xmlObject );

        final String result = XmlSerializers2.module().serialize( xmlObject );
        Assert.assertNotNull( xmlObject );

        assertXml( "module.xml", result );
    }
}
