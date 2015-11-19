package com.enonic.xp.xml.alias;


import org.junit.Test;

import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.Assert.*;

public class XmlAliasConvertersTest
{
    @Test
    public void testConvertersExist()
    {
        assertNotNull( XmlAliasConverters.getConverter( InputTypeName.CONTENT_SELECTOR ) );
        assertNotNull( XmlAliasConverters.getConverter( InputTypeName.from( "some-name" ) ) );
    }

    @Test
    public void testContentSelectorConverters()
    {
        final String contentType = "allowType";
        final String relationshipType = "relationship";
        final String shouldBeUnchanged = "relationshipX";
        final String result1 = XmlAliasConverters.convert( InputTypeName.CONTENT_SELECTOR, contentType );
        final String result2 = XmlAliasConverters.convert( InputTypeName.CONTENT_SELECTOR, relationshipType );
        final String result3 = XmlAliasConverters.convert( InputTypeName.CONTENT_SELECTOR, shouldBeUnchanged );

        assertEquals( "allow-content-type", result1 );
        assertEquals( "relationship-type", result2 );
        assertEquals( shouldBeUnchanged, result3 );
    }

    @Test
    public void testDefaultConverter()
    {
        final String type = "some-type";
        final String result = XmlAliasConverters.convert( InputTypeName.CONTENT_SELECTOR, type );

        assertEquals( type, result );
    }
}
