package com.enonic.xp.index;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HtmlStripperTest
{
    private HtmlStripper htmlStripper;

    @BeforeEach
    void setUp()
    {
        this.htmlStripper = new HtmlStripper();
    }

    @Test
    void processEmpty()
    {
        assertNull( this.htmlStripper.process( null ) );
        assertEquals( ValueFactory.newString( "" ), this.htmlStripper.process( ValueFactory.newString( "" ) ) );
        assertEquals( ValueFactory.newString( "" ), this.htmlStripper.process( ValueFactory.newString( "<tag/>" ) ) );
    }

    @Test
    void process()
    {
        assertEquals( ValueFactory.newString( "ValueWithoutTags" ),
                      this.htmlStripper.process( ValueFactory.newString( "ValueWithoutTags" ) ) );
        assertEquals( ValueFactory.newString( "Value" ), this.htmlStripper.process( ValueFactory.newString( "<a>Value</a>" ) ) );
        assertEquals( ValueFactory.newString( "TextBeforeTextBetweenTextAfter" ), this.htmlStripper.process( ValueFactory.newString(
            "<!-- Comment -->TextBefore<tag param=\"paramValue\">TextBetween</tag>TextAfter<EmptyNode/><SecondEmptyNode/>" ) ) );
    }

    @Test
    void processDifferentTypes()
    {
        Value valueToProcess = ValueFactory.newString( "abc<tag>def</tag><secondtag/>" );
        assertEquals( ValueFactory.newString( "abcdef" ), this.htmlStripper.process( valueToProcess ) );
        valueToProcess = ValueFactory.newXml( "<xml>xmlValue</xml>" );
        assertEquals( ValueFactory.newXml( "xmlValue" ), this.htmlStripper.process( valueToProcess ) );

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

    @Test
    void processEscapedCharacters()
    {
        Value valueToProcess = ValueFactory.newString( "&lt;tag value=\"&aelig;&oslash;&aring;\"/&gt;" );
        assertEquals( ValueFactory.newString( "<tag value=\"æøå\"/>" ), this.htmlStripper.process( valueToProcess ) );
        valueToProcess = ValueFactory.newXml( "&lt;tag value=\"&aelig;&oslash;&aring;\"/&gt;" );
        assertEquals( ValueFactory.newXml( "<tag value=\"æøå\"/>" ), this.htmlStripper.process( valueToProcess ) );
    }

    @Test
    void cornerCases()
    {
        Value valueToProcess = ValueFactory.newString( "<span>Test</span> <a about=\">\" href=\"#\">valid html</a>" );
        assertEquals( ValueFactory.newString( "Test valid html" ), this.htmlStripper.process( valueToProcess ) );

        valueToProcess = ValueFactory.newString( "Hey<p>I&apos;m so <b>happy</b>!</p>" );
        assertEquals( ValueFactory.newString( "Hey I'm so happy!" ), this.htmlStripper.process( valueToProcess ) );
    }
}
