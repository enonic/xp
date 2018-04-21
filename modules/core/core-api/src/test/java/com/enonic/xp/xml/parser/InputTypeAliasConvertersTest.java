package com.enonic.xp.xml.parser;


import org.junit.Test;

import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.Assert.*;

public class InputTypeAliasConvertersTest
{
    @Test
    public void testConvertersExist()
    {
        assertNotNull( InputTypeAliasConverters.getConverter( InputTypeName.CONTENT_SELECTOR ) );
        assertNotNull( InputTypeAliasConverters.getConverter( InputTypeName.from( "some-name" ) ) );
    }

    @Test
    public void testContentSelectorConverters()
    {
        final String contentType = "allowType";
        final String relationshipType = "relationship";
        final String shouldBeUnchanged = "relationshipX";
        final String result1 = InputTypeAliasConverters.convert( InputTypeName.CONTENT_SELECTOR, contentType );
        final String result2 = InputTypeAliasConverters.convert( InputTypeName.CONTENT_SELECTOR, relationshipType );
        final String result3 = InputTypeAliasConverters.convert( InputTypeName.CONTENT_SELECTOR, shouldBeUnchanged );

        assertEquals( "allow-content-type", result1 );
        assertEquals( "relationship-type", result2 );
        assertEquals( shouldBeUnchanged, result3 );
    }

    @Test
    public void testMediaSelectorConverters()
    {
        final String contentType = "allowType";
        final String relationshipType = "relationship";
        final String shouldBeUnchanged = "relationshipX";
        final String result1 = InputTypeAliasConverters.convert( InputTypeName.MEDIA_SELECTOR, contentType );
        final String result2 = InputTypeAliasConverters.convert( InputTypeName.MEDIA_SELECTOR, relationshipType );
        final String result3 = InputTypeAliasConverters.convert( InputTypeName.MEDIA_SELECTOR, shouldBeUnchanged );

        assertEquals( "allow-content-type", result1 );
        assertEquals( "relationship-type", result2 );
        assertEquals( shouldBeUnchanged, result3 );
    }

    @Test
    public void testImageSelectorConverters()
    {
        final String contentType = "allowType";
        final String relationshipType = "relationship";
        final String shouldBeUnchanged = "relationshipX";
        final String result1 = InputTypeAliasConverters.convert( InputTypeName.IMAGE_SELECTOR, contentType );
        final String result2 = InputTypeAliasConverters.convert( InputTypeName.IMAGE_SELECTOR, relationshipType );
        final String result3 = InputTypeAliasConverters.convert( InputTypeName.IMAGE_SELECTOR, shouldBeUnchanged );

        assertEquals( "allow-content-type", result1 );
        assertEquals( "relationship-type", result2 );
        assertEquals( shouldBeUnchanged, result3 );
    }

    @Test
    public void testDefaultConverter()
    {
        final String type = "some-type";
        final String result = InputTypeAliasConverters.convert( InputTypeName.CONTENT_SELECTOR, type );

        assertEquals( type, result );
    }
}
