package com.enonic.xp.xml.serializer;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.xml.model.XmlModule;

public class XmlModuleSerializerTest
    extends BaseXmlSerializerTest
{
    @Test
    public void test_parse_serialize()
        throws Exception
    {
        final String xml = readFromFile( "module.xml" );

        final XmlModule xmlObject = XmlSerializers.module().parse( xml );
        Assert.assertNotNull( xmlObject );

        final String result = XmlSerializers.module().serialize( xmlObject );
        Assert.assertNotNull( xmlObject );

        assertXml( "module.xml", result );
    }
}
