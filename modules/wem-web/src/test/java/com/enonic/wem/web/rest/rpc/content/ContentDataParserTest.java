package com.enonic.wem.web.rest.rpc.content;


import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.formitem.FormItemSet;
import com.enonic.wem.api.content.type.formitem.comptype.ComponentTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.web.json.ObjectMapperHelper;

import static com.enonic.wem.api.content.type.formitem.Component.newComponent;
import static com.enonic.wem.api.content.type.formitem.FormItemSet.newFormItemSet;
import static org.junit.Assert.*;

public class ContentDataParserTest
{
    @Test
    public void parse()
        throws IOException
    {
        ContentType contentType = new ContentType();
        contentType.addFormItem( newComponent().name( "myTextLine" ).type( ComponentTypes.TEXT_LINE ).build() );
        contentType.addFormItem( newComponent().name( "myTextArea" ).type( ComponentTypes.TEXT_AREA ).build() );
        contentType.addFormItem( newComponent().name( "myXml" ).type( ComponentTypes.XML ).build() );
        contentType.addFormItem( newComponent().name( "myDate" ).type( ComponentTypes.DATE ).build() );
        contentType.addFormItem( newComponent().name( "myWholeNumber" ).type( ComponentTypes.WHOLE_NUMBER ).build() );
        contentType.addFormItem( newComponent().name( "myDecimalNumber" ).type( ComponentTypes.DECIMAL_NUMBER ).build() );
        contentType.addFormItem( newComponent().name( "myGeoLocation" ).type( ComponentTypes.GEO_LOCATION ).build() );
        contentType.addFormItem( newComponent().name( "myColor" ).type( ComponentTypes.COLOR ).build() );
        FormItemSet mySet = newFormItemSet().name( "mySet" ).build();
        mySet.addFormItem( newComponent().name( "myTextLine" ).type( ComponentTypes.TEXT_LINE ).build() );
        contentType.addFormItem( mySet );

        StringBuilder json = new StringBuilder();
        json.append( "{" ).append( "\n" );
        json.append( "\"myTextLine\": \"Text line\"," ).append( "\n" );
        json.append( "\"myTextArea\": \"First line\\nSecond line\"," ).append( "\n" );
        json.append( "\"myXml\": \"<root>XML</root>\"," ).append( "\n" );
        json.append( "\"myDate\": \"2012-08-31\"," ).append( "\n" );
        json.append( "\"myWholeNumber\": \"1\"," ).append( "\n" );
        json.append( "\"myDecimalNumber\": \"1.1\"," ).append( "\n" );
        json.append( "\"myGeoLocation.latitude\": \"90\"," ).append( "\n" );
        json.append( "\"myGeoLocation.longitude\": \"180\"," ).append( "\n" );
        json.append( "\"myColor.red\": \"40\"," ).append( "\n" );
        json.append( "\"myColor.green\": \"60\"," ).append( "\n" );
        json.append( "\"myColor.blue\": \"80\"," ).append( "\n" );
        json.append( "\"mySet.myTextLine\": \"Inner line\"" ).append( "\n" );
        json.append( "}" );

        ObjectMapper objectMapper = ObjectMapperHelper.create();
        ObjectNode objectNode = objectMapper.readValue( json.toString(), ObjectNode.class );

        // exercise
        ContentDataParser contentDataParser = new ContentDataParser( contentType );
        ContentData parsedContentData = contentDataParser.parse( objectNode );

        // verify
        assertEquals( "Text line", parsedContentData.getData( new EntryPath( "myTextLine" ) ).getValue() );
        assertEquals( "First line\n" + "Second line", parsedContentData.getData( new EntryPath( "myTextArea" ) ).getValue() );
        assertEquals( new DateMidnight( 2012, 8, 31 ), parsedContentData.getData( new EntryPath( "myDate" ) ).getValue() );
        assertEquals( "<root>XML</root>", parsedContentData.getData( new EntryPath( "myXml" ) ).getValue() );
        assertEquals( 1L, parsedContentData.getData( new EntryPath( "myWholeNumber" ) ).getValue() );
        assertEquals( 1.1, parsedContentData.getData( new EntryPath( "myDecimalNumber" ) ).getValue() );
        assertEquals( 90.0, parsedContentData.getData( new EntryPath( "myGeoLocation.latitude" ) ).getValue() );
        assertEquals( 180.0, parsedContentData.getData( new EntryPath( "myGeoLocation.longitude" ) ).getValue() );
        assertEquals( 40l, parsedContentData.getData( new EntryPath( "myColor.red" ) ).getValue() );
        assertEquals( 60l, parsedContentData.getData( new EntryPath( "myColor.green" ) ).getValue() );
        assertEquals( 80l, parsedContentData.getData( new EntryPath( "myColor.blue" ) ).getValue() );
        assertEquals( "Inner line", parsedContentData.getData( new EntryPath( "mySet.myTextLine" ) ).getValue() );

        Data myColor = parsedContentData.getData( new EntryPath( "myColor" ) );
        Data myColorBlue = myColor.getDataSet().getData( "blue" );
        assertEquals( 80l, myColorBlue.getValue() );
    }

    @Test
    public void wholeNumber_within_formItemSet()
        throws IOException
    {
        ContentType myContentType = new ContentType();
        myContentType.setModule( Module.newModule().name( "myModule" ).build() );
        myContentType.setName( "myContentType" );
        FormItemSet formItemSet = newFormItemSet().name( "myFormItemSet" ).build();
        formItemSet.addFormItem( newComponent().name( "myWholeNumber" ).type( ComponentTypes.WHOLE_NUMBER ).build() );
        myContentType.addFormItem( formItemSet );

        StringBuilder json = new StringBuilder();
        json.append( "{" );
        json.append( "\"myFormItemSet.myWholeNumber\": \"1\"" );
        json.append( "}" );
        ObjectMapper objectMapper = ObjectMapperHelper.create();
        ObjectNode objectNode = objectMapper.readValue( json.toString(), ObjectNode.class );

        // exercise
        ContentDataParser contentDataParser = new ContentDataParser( myContentType );
        ContentData parsedContentData = contentDataParser.parse( objectNode );

        // verify
        assertEquals( 1l, parsedContentData.getData( new EntryPath( "myFormItemSet.myWholeNumber" ) ).getValue() );
    }

    @Test
    public void geoLocation()
        throws IOException
    {
        ContentType contentType = new ContentType();
        contentType.addFormItem( newComponent().name( "myGeoLocation" ).type( ComponentTypes.GEO_LOCATION ).build() );

        StringBuilder json = new StringBuilder();
        json.append( "{" ).append( "\n" );
        json.append( "\"myGeoLocation\": {" ).append( "\n" );
        json.append( "  \"latitude\": \"40.446195\"," ).append( "\n" );
        json.append( "  \"longitude\": \"-79.948862\"" ).append( "\n" );
        json.append( "  }" ).append( "\n" );
        json.append( "}" );
        ObjectMapper objectMapper = ObjectMapperHelper.create();
        ObjectNode objectNode = objectMapper.readValue( json.toString(), ObjectNode.class );

        // exercise
        ContentDataParser contentDataParser = new ContentDataParser( contentType );
        ContentData parsedContentData = contentDataParser.parse( objectNode );

        // verify

        assertEquals( 40.446195, parsedContentData.getData( new EntryPath( "myGeoLocation.latitude" ) ).getValue() );
        assertEquals( -79.948862, parsedContentData.getData( new EntryPath( "myGeoLocation.longitude" ) ).getValue() );
    }

    @Test
    public void parse_color()
        throws IOException
    {
        ContentType contentType = new ContentType();
        contentType.addFormItem( newComponent().name( "myColor" ).type( ComponentTypes.COLOR ).required( true ).build() );

        StringBuilder json = new StringBuilder();
        json.append( "{" ).append( "\n" );
        json.append( "\"myColor\": {" ).append( "\n" );
        json.append( "  \"red\": \"40\"," ).append( "\n" );
        json.append( "  \"green\": \"60\"," ).append( "\n" );
        json.append( "  \"blue\": \"80\"" ).append( "\n" );
        json.append( "  }" ).append( "\n" );
        json.append( "}" );
        ObjectMapper objectMapper = ObjectMapperHelper.create();
        ObjectNode objectNode = objectMapper.readValue( json.toString(), ObjectNode.class );

        // exercise
        ContentDataParser contentDataParser = new ContentDataParser( contentType );
        ContentData parsedContentData = contentDataParser.parse( objectNode );

        // verify
        assertEquals( 40l, parsedContentData.getData( new EntryPath( "myColor.red" ) ).getValue() );
        assertEquals( 60l, parsedContentData.getData( new EntryPath( "myColor.green" ) ).getValue() );
        assertEquals( 80l, parsedContentData.getData( new EntryPath( "myColor.blue" ) ).getValue() );
    }

    @Test
    public void parse_color_within_formItemSet()
        throws IOException
    {
        ContentType contentType = new ContentType();
        FormItemSet formItemSet = newFormItemSet().name( "myFormItemSet" ).build();
        contentType.addFormItem( formItemSet );
        formItemSet.addFormItem( newComponent().name( "myColor" ).type( ComponentTypes.COLOR ).required( true ).build() );

        StringBuilder json = new StringBuilder();
        json.append( "{" ).append( "\n" );
        json.append( "\"myFormItemSet.myColor\": {" ).append( "\n" );
        json.append( "  \"red\": \"40\"," ).append( "\n" );
        json.append( "  \"green\": \"60\"," ).append( "\n" );
        json.append( "  \"blue\": \"80\"" ).append( "\n" );
        json.append( "  }" ).append( "\n" );
        json.append( "}" );
        ObjectMapper objectMapper = ObjectMapperHelper.create();
        ObjectNode objectNode = objectMapper.readValue( json.toString(), ObjectNode.class );

        // exercise
        ContentDataParser contentDataParser = new ContentDataParser( contentType );
        ContentData parsedContentData = contentDataParser.parse( objectNode );

        // verify
        assertEquals( 40l, parsedContentData.getData( new EntryPath( "myFormItemSet.myColor.red" ) ).getValue() );
        assertEquals( 60l, parsedContentData.getData( new EntryPath( "myFormItemSet.myColor.green" ) ).getValue() );
        assertEquals( 80l, parsedContentData.getData( new EntryPath( "myFormItemSet.myColor.blue" ) ).getValue() );
    }
}
