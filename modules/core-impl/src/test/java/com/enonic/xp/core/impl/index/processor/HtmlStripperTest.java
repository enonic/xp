package com.enonic.xp.core.impl.index.processor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class HtmlStripperTest
{
    HtmlStripper htmlStripper;

    @Before
    public void setUp()
    {
        this.htmlStripper = new HtmlStripper();
    }

    @Test
    public void processEmpty()
    {
        Value emptyStringValue = Value.newString( "" );

        assertNull( this.htmlStripper.process( null ) );
        assertEquals( emptyStringValue, this.htmlStripper.process( Value.newString( "" ) ) );
        assertEquals( emptyStringValue, this.htmlStripper.process( Value.newString( "<tag/>" ) ) );
    }

    @Test
    public void process()
    {
        assertEquals( Value.newString( "ValueWithoutTags" ), this.htmlStripper.process( Value.newString( "ValueWithoutTags" ) ) );
        assertEquals( Value.newString( "Value" ), this.htmlStripper.process( Value.newString( "<a>Value</a>" ) ) );
        assertEquals( Value.newString( "TextBeforeTextBetweenTextAfter" ), this.htmlStripper.process(
            Value.newString( "<!-- Comment -->TextBefore<tag param=\"paramValue\">TextBetween</tag>TextAfter<EmptyNode/>" ) ) );
    }

    public void processDifferentTypes()
    {
        Value valueToProcess = Value.newString( "abc<tag>def</tag>" );
        assertEquals( Value.newString( "abcdef" ), valueToProcess );
        valueToProcess = Value.newHtmlPart( "<div>abc</div>" );
        assertEquals( Value.newHtmlPart( "abc" ), valueToProcess );
        valueToProcess = Value.newXml( "<xml>xmlValue</xml>" );
        assertEquals( Value.newXml( "xmlValue" ), valueToProcess );

        valueToProcess = Value.newBoolean( false );
        assertEquals( valueToProcess, valueToProcess );
        valueToProcess = Value.newReference( Reference.from( "abc" ) );
        assertEquals( valueToProcess, valueToProcess );
        valueToProcess = Value.newGeoPoint( GeoPoint.from( "1.1,-1.1" ) );
        assertEquals( valueToProcess, valueToProcess );
        valueToProcess = Value.newDouble( 1.1 );
        assertEquals( valueToProcess, valueToProcess );
        valueToProcess = Value.newLong( 1L );
        assertEquals( valueToProcess, valueToProcess );
        valueToProcess = Value.newLocalDate( LocalDate.of( 2012, 1, 1 ) );
        assertEquals( valueToProcess, valueToProcess );
        valueToProcess = Value.newLocalDateTime( LocalDateTime.of( 2012, 1, 1, 12, 0, 0 ) );
        assertEquals( valueToProcess, valueToProcess );
    }
}
