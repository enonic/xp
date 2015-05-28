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
    private HtmlStripper htmlStripper;

    @Before
    public void setUp()
    {
        this.htmlStripper = new HtmlStripper();
    }

    @Test
    public void processEmpty()
    {
        assertNull( this.htmlStripper.process( null ) );
        assertEquals( Value.newString( "" ), this.htmlStripper.process( Value.newString( "" ) ) );
        assertEquals( Value.newString( " " ), this.htmlStripper.process( Value.newString( "<tag/>" ) ) );
    }

    @Test
    public void process()
    {
        assertEquals( Value.newString( "ValueWithoutTags" ), this.htmlStripper.process( Value.newString( "ValueWithoutTags" ) ) );
        assertEquals( Value.newString( " Value " ), this.htmlStripper.process( Value.newString( "<a>Value</a>" ) ) );
        assertEquals( Value.newString( " TextBefore TextBetween TextAfter " ), this.htmlStripper.process( Value.newString(
            "<!-- Comment -->TextBefore<tag param=\"paramValue\">TextBetween</tag>TextAfter<EmptyNode/><SecondEmptyNode/>" ) ) );
    }

    @Test
    public void processDifferentTypes()
    {
        Value valueToProcess = Value.newString( "abc<tag>def</tag><secondtag/>" );
        assertEquals( Value.newString( "abc def " ), this.htmlStripper.process( valueToProcess ) );
        valueToProcess = Value.newXml( "<xml>xmlValue</xml>" );
        assertEquals( Value.newXml( " xmlValue " ), this.htmlStripper.process( valueToProcess ) );

        valueToProcess = Value.newBoolean( false );
        assertEquals( valueToProcess, this.htmlStripper.process( valueToProcess ) );
        valueToProcess = Value.newReference( Reference.from( "abc" ) );
        assertEquals( valueToProcess, this.htmlStripper.process( valueToProcess ) );
        valueToProcess = Value.newGeoPoint( GeoPoint.from( "1.1,-1.1" ) );
        assertEquals( valueToProcess, this.htmlStripper.process( valueToProcess ) );
        valueToProcess = Value.newDouble( 1.1 );
        assertEquals( valueToProcess, this.htmlStripper.process( valueToProcess ) );
        valueToProcess = Value.newLong( 1L );
        assertEquals( valueToProcess, this.htmlStripper.process( valueToProcess ) );
        valueToProcess = Value.newLocalDate( LocalDate.of( 2012, 1, 1 ) );
        assertEquals( valueToProcess, this.htmlStripper.process( valueToProcess ) );
        valueToProcess = Value.newLocalDateTime( LocalDateTime.of( 2012, 1, 1, 12, 0, 0 ) );
        assertEquals( valueToProcess, this.htmlStripper.process( valueToProcess ) );
    }
}
