package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.XmlTestHelper;

import static org.junit.Assert.*;

public class MixinXmlSerializerTest
    extends AbstractMixinSerializerTest
{

    private XmlTestHelper xmlTestHelper = new XmlTestHelper( this );

    @Override
    MixinSerializer getSerializer()
    {
        return new MixinXmlSerializer().prettyPrint( true );
    }

    @Override
    void assertSerializedResult( final String fileNameForExpected, final String actualSerialization )
    {
        assertEquals( loadTestXml( fileNameForExpected + ".xml" ), actualSerialization );
    }

    @Override
    String getSerializedString( final String fileName )
    {
        return xmlTestHelper.loadTestFile( fileName + ".xml" );
    }
}
