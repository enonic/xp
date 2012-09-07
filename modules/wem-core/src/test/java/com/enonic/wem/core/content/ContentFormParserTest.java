package com.enonic.wem.core.content;


import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.core.content.data.ContentData;
import com.enonic.wem.core.content.data.ContentFormParser;
import com.enonic.wem.core.content.data.EntryPath;
import com.enonic.wem.core.content.type.ContentType;
import com.enonic.wem.core.content.type.formitem.fieldtype.FieldTypes;

import static com.enonic.wem.core.content.type.formitem.Component.newField;
import static org.junit.Assert.*;

public class ContentFormParserTest
{
    @Test
    public void parse()
    {
        ContentType contentType = new ContentType();
        contentType.addFormItem( newField().name( "myTextLine" ).type( FieldTypes.TEXT_LINE ).build() );
        contentType.addFormItem( newField().name( "myTextArea" ).type( FieldTypes.TEXT_AREA ).build() );
        contentType.addFormItem( newField().name( "myXml" ).type( FieldTypes.XML ).build() );
        contentType.addFormItem( newField().name( "myDate" ).type( FieldTypes.DATE ).build() );
        contentType.addFormItem( newField().name( "myWholeNumber" ).type( FieldTypes.WHOLE_NUMBER ).build() );
        contentType.addFormItem( newField().name( "myDecimalNumber" ).type( FieldTypes.DECIMAL_NUMBER ).build() );
        contentType.addFormItem( newField().name( "myGeoLocation" ).type( FieldTypes.GEO_LOCATION ).build() );

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
