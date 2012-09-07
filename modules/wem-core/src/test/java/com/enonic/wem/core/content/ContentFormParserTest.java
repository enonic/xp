package com.enonic.wem.core.content;


import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.core.content.data.ContentData;
import com.enonic.wem.core.content.data.ContentFormParser;
import com.enonic.wem.core.content.data.EntryPath;
import com.enonic.wem.core.content.type.ContentType;
import com.enonic.wem.core.content.type.formitem.comptype.ComponentTypes;

import static com.enonic.wem.core.content.type.formitem.Component.newComponent;
import static org.junit.Assert.*;

public class ContentFormParserTest
{
    @Test
    public void parse()
    {
        ContentType contentType = new ContentType();
        contentType.addFormItem( newComponent().name( "myTextLine" ).type( ComponentTypes.TEXT_LINE ).build() );
        contentType.addFormItem( newComponent().name( "myTextArea" ).type( ComponentTypes.TEXT_AREA ).build() );
        contentType.addFormItem( newComponent().name( "myXml" ).type( ComponentTypes.XML ).build() );
        contentType.addFormItem( newComponent().name( "myDate" ).type( ComponentTypes.DATE ).build() );
        contentType.addFormItem( newComponent().name( "myWholeNumber" ).type( ComponentTypes.WHOLE_NUMBER ).build() );
        contentType.addFormItem( newComponent().name( "myDecimalNumber" ).type( ComponentTypes.DECIMAL_NUMBER ).build() );
        contentType.addFormItem( newComponent().name( "myGeoLocation" ).type( ComponentTypes.GEO_LOCATION ).build() );

        Map<String, String> submittedValues = new LinkedHashMap<String, String>();
        submittedValues.put( "myTextLine", "Text line" );
        submittedValues.put( "myTextArea", "First line\nSecond line" );
        submittedValues.put( "myXml", "<root>XML</root>" );
        submittedValues.put( "myDate", "2012-08-31" );
        submittedValues.put( "myWholeNumber", "13" );
        submittedValues.put( "myDecimalNumber", "13.12" );
        submittedValues.put( "myGeoLocation", "40.446195, -79.948862" );

        // exercise
        ContentFormParser contentFormParser = new ContentFormParser( contentType );
        ContentData contentData = contentFormParser.parse( submittedValues );

        // verify
        assertEquals( "Text line", contentData.getData( new EntryPath( "myTextLine" ) ).getValue() );
        assertEquals( "First line\n" + "Second line", contentData.getData( new EntryPath( "myTextArea" ) ).getValue() );
        assertEquals( new DateMidnight( 2012, 8, 31 ), contentData.getData( new EntryPath( "myDate" ) ).getValue() );
        assertEquals( "<root>XML</root>", contentData.getData( new EntryPath( "myXml" ) ).getValue() );
        assertEquals( 13L, contentData.getData( new EntryPath( "myWholeNumber" ) ).getValue() );
        assertEquals( 13.12, contentData.getData( new EntryPath( "myDecimalNumber" ) ).getValue() );
        assertEquals( "40.446195, -79.948862", contentData.getData( new EntryPath( "myGeoLocation" ) ).getValue() );
    }
}
