package com.enonic.xp.index;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
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
        assertEquals( ValueFactory.newString( "" ), this.htmlStripper.process( ValueFactory.newString( "" ) ) );
        assertEquals( ValueFactory.newString( " " ), this.htmlStripper.process( ValueFactory.newString( "<tag/>" ) ) );
    }

    @Test
    public void process()
    {
        assertEquals( ValueFactory.newString( "ValueWithoutTags" ), this.htmlStripper.process( ValueFactory.newString( "ValueWithoutTags" ) ) );
        assertEquals( ValueFactory.newString( " Value " ), this.htmlStripper.process( ValueFactory.newString( "<a>Value</a>" ) ) );
        assertEquals( ValueFactory.newString( " TextBefore TextBetween TextAfter " ), this.htmlStripper.process( ValueFactory.newString(
            "<!-- Comment -->TextBefore<tag param=\"paramValue\">TextBetween</tag>TextAfter<EmptyNode/><SecondEmptyNode/>" ) ) );
    }

    @Test
    public void processDifferentTypes()
    {
        Value valueToProcess = ValueFactory.newString( "abc<tag>def</tag><secondtag/>" );
        assertEquals( ValueFactory.newString( "abc def " ), this.htmlStripper.process( valueToProcess ) );
        valueToProcess = ValueFactory.newXml( "<xml>xmlValue</xml>" );
        assertEquals( ValueFactory.newXml( " xmlValue " ), this.htmlStripper.process( valueToProcess ) );

        valueToProcess = ValueFactory.newBoolean( false );
        assertEquals( valueToProcess, this.htmlStripper.process( valueToProcess ) );
        valueToProcess = ValueFactory.newReference( Reference.from( "abc" ) );
        assertEquals( valueToProcess, this.htmlStripper.process( valueToProcess ) );
        valueToProcess = ValueFactory.newGeoPoint( GeoPoint.from( "1.1,-1.1" ) );
        assertEquals( valueToProcess, this.htmlStripper.process( valueToProcess ) );
        valueToProcess = ValueFactory.newDouble( 1.1 );
        assertEquals( valueToProcess, this.htmlStripper.process( valueToProcess ) );
        valueToProcess = ValueFactory.newLong( 1L );
        assertEquals( valueToProcess, this.htmlStripper.process( valueToProcess ) );
        valueToProcess = ValueFactory.newLocalDate( LocalDate.of( 2012, 1, 1 ) );
        assertEquals( valueToProcess, this.htmlStripper.process( valueToProcess ) );
        valueToProcess = ValueFactory.newLocalDateTime( LocalDateTime.of( 2012, 1, 1, 12, 0, 0 ) );
        assertEquals( valueToProcess, this.htmlStripper.process( valueToProcess ) );
    }
}
