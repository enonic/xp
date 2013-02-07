package com.enonic.wem.web.rest.rpc.content;


import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateMidnight;
import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.web.json.ObjectMapperHelper;

import static com.enonic.wem.api.content.type.ContentType.newContentType;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static org.junit.Assert.*;

public class RootDataSetParserTest
{
    @Test
    public void parse_simple_types()
        throws IOException
    {
        final FormItemSet mySet = newFormItemSet().name( "mySet" ).build();
        mySet.add( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );

        final ContentType contentType = newContentType().
            module( ModuleName.from( "myModule" ) ).
            name( "myContentType" ).
            addFormItem( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() ).
            addFormItem( newInput().name( "myTextArea" ).type( InputTypes.TEXT_AREA ).build() ).
            addFormItem( newInput().name( "myXml" ).type( InputTypes.XML ).build() ).
            addFormItem( newInput().name( "myDate" ).type( InputTypes.DATE ).build() ).
            addFormItem( newInput().name( "myWholeNumber" ).type( InputTypes.WHOLE_NUMBER ).build() ).
            addFormItem( newInput().name( "myDecimalNumber" ).type( InputTypes.DECIMAL_NUMBER ).build() ).
            addFormItem( mySet ).
            build();

        StringBuilder json = new StringBuilder();
        json.append( "{" ).append( "\n" );
        json.append( "\"myTextLine\": \"Text line\"," ).append( "\n" );
        json.append( "\"myTextArea\": \"First line\\nSecond line\"," ).append( "\n" );
        json.append( "\"myXml\": \"<root>XML</root>\"," ).append( "\n" );
        json.append( "\"myDate\": \"2012-08-31\"," ).append( "\n" );
        json.append( "\"myWholeNumber\": \"1\"," ).append( "\n" );
        json.append( "\"myDecimalNumber\": \"1.1\"," ).append( "\n" );
        json.append( "\"mySet.myTextLine\": \"Inner line\"" ).append( "\n" );
        json.append( "}" );

        ObjectMapper objectMapper = ObjectMapperHelper.create();
        ObjectNode objectNode = objectMapper.readValue( json.toString(), ObjectNode.class );

        // exercise
        RootDataSetParser rootDataSetParser = new RootDataSetParser( contentType );
        DataSet parsedRootDataSet = rootDataSetParser.parse( objectNode );

        // verify
        assertEquals( "Text line", parsedRootDataSet.getData( EntryPath.from( "myTextLine" ) ).getObject() );
        assertEquals( "First line\n" + "Second line", parsedRootDataSet.getData( EntryPath.from( "myTextArea" ) ).getObject() );
        assertEquals( new DateMidnight( 2012, 8, 31 ), parsedRootDataSet.getData( EntryPath.from( "myDate" ) ).getObject() );
        assertEquals( "<root>XML</root>", parsedRootDataSet.getData( EntryPath.from( "myXml" ) ).getObject() );
        assertEquals( 1L, parsedRootDataSet.getData( EntryPath.from( "myWholeNumber" ) ).getObject() );
        assertEquals( 1.1, parsedRootDataSet.getData( EntryPath.from( "myDecimalNumber" ) ).getObject() );
        assertEquals( "Inner line", parsedRootDataSet.getData( EntryPath.from( "mySet.myTextLine" ) ).getObject() );
    }

    @Test
    @Ignore
    public void pars_advanced_types()
        throws IOException
    {
        final FormItemSet mySet = newFormItemSet().name( "mySet" ).build();
        mySet.add( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );

        final ContentType contentType = newContentType().
            module( ModuleName.from( "myModule" ) ).
            name( "myContentType" ).
            addFormItem( newInput().name( "myGeoLocation" ).type( InputTypes.GEO_LOCATION ).build() ).
            addFormItem( newInput().name( "myColor" ).type( InputTypes.COLOR ).build() ).
            addFormItem( mySet ).
            build();

        StringBuilder json = new StringBuilder();
        json.append( "{" ).append( "\n" );
        json.append( "\"myGeoLocation.latitude\": \"90\"," ).append( "\n" );
        json.append( "\"myGeoLocation.longitude\": \"180\"," ).append( "\n" );
        json.append( "\"myColor.red\": \"40\"," ).append( "\n" );
        json.append( "\"myColor.green\": \"60\"," ).append( "\n" );
        json.append( "\"myColor.blue\": \"80\"" ).append( "\n" );
        json.append( "}" );

        ObjectMapper objectMapper = ObjectMapperHelper.create();
        ObjectNode objectNode = objectMapper.readValue( json.toString(), ObjectNode.class );

        // exercise
        RootDataSetParser rootDataSetParser = new RootDataSetParser( contentType );
        DataSet parsedContentData = rootDataSetParser.parse( objectNode );

        // verify
        assertEquals( 90.0, parsedContentData.getData( EntryPath.from( "myGeoLocation.latitude" ) ).getObject() );
        assertEquals( 180.0, parsedContentData.getData( EntryPath.from( "myGeoLocation.longitude" ) ).getObject() );
        assertEquals( 40l, parsedContentData.getData( EntryPath.from( "myColor.red" ) ).getObject() );
        assertEquals( 60l, parsedContentData.getData( EntryPath.from( "myColor.green" ) ).getObject() );
        assertEquals( 80l, parsedContentData.getData( EntryPath.from( "myColor.blue" ) ).getObject() );

        Data myColor = parsedContentData.getData( EntryPath.from( "myColor" ) );
        Data myColorBlue = myColor.toDataSet().getData( "blue" );
        assertEquals( 80l, myColorBlue.getObject() );
    }

    @Test
    public void wholeNumber_within_formItemSet()
        throws IOException
    {
        final FormItemSet formItemSet = newFormItemSet().name( "myFormItemSet" ).build();
        formItemSet.add( newInput().name( "myWholeNumber" ).type( InputTypes.WHOLE_NUMBER ).build() );
        final ContentType myContentType = newContentType().
            module( ModuleName.from( "myModule" ) ).
            name( "myContentType" ).
            addFormItem( formItemSet ).
            build();

        StringBuilder json = new StringBuilder();
        json.append( "{" );
        json.append( "\"myFormItemSet.myWholeNumber\": \"1\"" );
        json.append( "}" );
        ObjectMapper objectMapper = ObjectMapperHelper.create();
        ObjectNode objectNode = objectMapper.readValue( json.toString(), ObjectNode.class );

        // exercise
        RootDataSetParser rootDataSetParser = new RootDataSetParser( myContentType );
        DataSet parsedContentData = rootDataSetParser.parse( objectNode );

        // verify
        assertEquals( 1l, parsedContentData.getData( EntryPath.from( "myFormItemSet.myWholeNumber" ) ).getObject() );
    }

    @Test
    @Ignore
    public void geoLocation()
        throws IOException
    {
        final ContentType contentType = newContentType().
            module( ModuleName.from( "myModule" ) ).
            name( "myContentType" ).
            addFormItem( newInput().name( "myGeoLocation" ).type( InputTypes.GEO_LOCATION ).build() ).build();

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
        RootDataSetParser rootDataSetParser = new RootDataSetParser( contentType );
        DataSet parsedContentData = rootDataSetParser.parse( objectNode );

        // verify

        assertEquals( 40.446195, parsedContentData.getData( EntryPath.from( "myGeoLocation.latitude" ) ).getObject() );
        assertEquals( -79.948862, parsedContentData.getData( EntryPath.from( "myGeoLocation.longitude" ) ).getObject() );
    }

    @Test
    @Ignore
    public void parse_color()
        throws IOException
    {
        final ContentType contentType = newContentType().
            module( ModuleName.from( "myModule" ) ).
            name( "myContentType" ).
            addFormItem( newInput().name( "myColor" ).type( InputTypes.COLOR ).required( true ).build() ).build();

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
        RootDataSetParser rootDataSetParser = new RootDataSetParser( contentType );
        DataSet parsedContentData = rootDataSetParser.parse( objectNode );

        // verify
        assertEquals( 40l, parsedContentData.getData( EntryPath.from( "myColor.red" ) ).getObject() );
        assertEquals( 60l, parsedContentData.getData( EntryPath.from( "myColor.green" ) ).getObject() );
        assertEquals( 80l, parsedContentData.getData( EntryPath.from( "myColor.blue" ) ).getObject() );
    }

    @Test
    @Ignore
    public void parse_color_within_formItemSet()
        throws IOException
    {
        final FormItemSet formItemSet = newFormItemSet().name( "myFormItemSet" ).build();
        formItemSet.add( newInput().name( "myColor" ).type( InputTypes.COLOR ).required( true ).build() );
        final ContentType contentType = newContentType().
            module( ModuleName.from( "myModule" ) ).
            name( "myContentType" ).
            addFormItem( formItemSet ).
            build();

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
        RootDataSetParser rootDataSetParser = new RootDataSetParser( contentType );
        DataSet parsedDataSet = rootDataSetParser.parse( objectNode );

        // verify
        assertEquals( 40l, parsedDataSet.getData( EntryPath.from( "myFormItemSet.myColor.red" ) ).getObject() );
        assertEquals( 60l, parsedDataSet.getData( EntryPath.from( "myFormItemSet.myColor.green" ) ).getObject() );
        assertEquals( 80l, parsedDataSet.getData( EntryPath.from( "myFormItemSet.myColor.blue" ) ).getObject() );
    }
}
