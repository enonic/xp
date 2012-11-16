package com.enonic.wem.core.content.type;

import org.jdom.Document;

import com.enonic.cms.framework.util.JDOMUtil;

import static org.junit.Assert.assertEquals;

public class ContentTypeXmlSerializerTest
    extends AbstractContentTypeSerializerTest
{
    @Override
    ContentTypeSerializer getSerializer()
    {
        return new ContentTypeXmlSerializer().prettyPrint( true );
    }

    @Override
    void assertSerializedResult( final String fileNameForExpected, final String actualSerialization )
    {
        assertEquals( getXmlAsString( "contentType-allBaseTypes.xml" ), actualSerialization );
    }

    protected String getXmlAsString( String fileName )
    {
        try
        {
            Document document = JDOMUtil.parseDocument( getClass().getResource( fileName ).openStream() );
            return JDOMUtil.prettyPrintDocument( document );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}
