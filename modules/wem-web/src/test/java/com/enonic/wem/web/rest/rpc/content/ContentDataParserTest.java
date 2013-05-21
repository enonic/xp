package com.enonic.wem.web.rest.rpc.content;


import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateMidnight;
import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.DataPath;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.type.ValueTypes;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.form.FormItemSet;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.form.inputtype.RelationshipConfig;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.web.json.ObjectMapperHelper;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.content.form.inputtype.ImageConfig.newImageConfig;
import static com.enonic.wem.api.schema.content.form.inputtype.RelationshipConfig.newRelationshipConfig;
import static org.junit.Assert.*;

public class ContentDataParserTest
{
    private static final String CONTENT_ID = "edda7c84-d1ef-4d4b-b79e-71b696a716df";

    @Test
    public void parse_simple_types()
        throws IOException
    {
        RelationshipConfig relationshipConfig = newRelationshipConfig().relationshipType( QualifiedRelationshipTypeName.LIKE ).build();

        FormItemSet mySet = newFormItemSet().name( "mySet" ).build();
        mySet.add( newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() );

        final ContentType contentType = newContentType().
            module( ModuleName.from( "mymodule" ) ).
            name( "my_content_type" ).
            addFormItem( newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( newInput().name( "myTextArea" ).inputType( InputTypes.TEXT_AREA ).build() ).
            addFormItem( newInput().name( "myXml" ).inputType( InputTypes.XML ).build() ).
            addFormItem( newInput().name( "myDate" ).inputType( InputTypes.DATE ).build() ).
            addFormItem( newInput().name( "myWholeNumber" ).inputType( InputTypes.WHOLE_NUMBER ).build() ).
            addFormItem( newInput().name( "myDecimalNumber" ).inputType( InputTypes.DECIMAL_NUMBER ).build() ).
            addFormItem(
                newInput().name( "myRelationship" ).inputType( InputTypes.RELATIONSHIP ).inputTypeConfig( relationshipConfig ).build() ).
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
        json.append( "\"myRelationship\": \"ABCDEF\"," ).append( "\n" );
        json.append( "\"mySet.myTextLine\": \"Inner line\"" ).append( "\n" );
        json.append( "}" );

        ObjectMapper objectMapper = ObjectMapperHelper.create();
        ObjectNode objectNode = objectMapper.readValue( json.toString(), ObjectNode.class );

        // exercise
        ContentDataParser contentDataParser = new ContentDataParser( contentType );
        ContentData parsedContentData = contentDataParser.parse( objectNode );

        // verify
        assertEquals( "Text line", parsedContentData.getProperty( DataPath.from( "myTextLine" ) ).getObject() );
        assertEquals( "First line\n" + "Second line", parsedContentData.getProperty( DataPath.from( "myTextArea" ) ).getObject() );
        assertEquals( new DateMidnight( 2012, 8, 31 ), parsedContentData.getProperty( DataPath.from( "myDate" ) ).getObject() );
        assertEquals( "<root>XML</root>", parsedContentData.getProperty( DataPath.from( "myXml" ) ).getObject() );
        assertEquals( 1L, parsedContentData.getProperty( DataPath.from( "myWholeNumber" ) ).getObject() );
        assertEquals( 1.1, parsedContentData.getProperty( DataPath.from( "myDecimalNumber" ) ).getObject() );
        assertEquals( ContentId.from( "ABCDEF" ), parsedContentData.getProperty( DataPath.from( "myRelationship" ) ).getObject() );
        assertEquals( "Inner line", parsedContentData.getProperty( DataPath.from( "mySet.myTextLine" ) ).getObject() );
    }

    @Test
    public void parse_Image()
        throws IOException
    {
        final ContentType contentType = newContentType().
            module( ModuleName.from( "mymodule" ) ).
            name( "my_content_type" ).
            addFormItem( newInput().name( "myImage" ).inputType( InputTypes.IMAGE ).inputTypeConfig( newImageConfig().
                relationshipType( QualifiedRelationshipTypeName.DEFAULT ).
                build() ).build() ).
            build();

        StringBuilder json = new StringBuilder();
        json.append( "{" ).append( "\n" );
        json.append( "\"myImage\": \"" + CONTENT_ID + "\"" ).append( "\n" );
        json.append( "}" );

        ObjectMapper objectMapper = ObjectMapperHelper.create();
        ObjectNode objectNode = objectMapper.readValue( json.toString(), ObjectNode.class );

        // exercise
        ContentDataParser contentDataParser = new ContentDataParser( contentType );
        DataSet parsedContentData = contentDataParser.parse( objectNode );

        // verify
        assertEquals( ContentId.from( CONTENT_ID ), parsedContentData.getProperty( DataPath.from( "myImage" ) ).getObject() );
    }

    @Test
    @Ignore
    public void pars_advanced_types()
        throws IOException
    {
        final FormItemSet mySet = newFormItemSet().name( "mySet" ).build();
        mySet.add( newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() );

        final ContentType contentType = newContentType().
            module( ModuleName.from( "mymodule" ) ).
            name( "my_content_type" ).
            addFormItem( newInput().name( "myGeoLocation" ).inputType( InputTypes.GEO_LOCATION ).build() ).
            addFormItem( newInput().name( "myColor" ).inputType( InputTypes.COLOR ).build() ).
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
        ContentDataParser contentDataParser = new ContentDataParser( contentType );
        DataSet parsedContentData = contentDataParser.parse( objectNode );

        // verify
        assertEquals( 90.0, parsedContentData.getProperty( DataPath.from( "myGeoLocation.latitude" ) ).getObject() );
        assertEquals( 180.0, parsedContentData.getProperty( DataPath.from( "myGeoLocation.longitude" ) ).getObject() );
        assertEquals( 40l, parsedContentData.getProperty( DataPath.from( "myColor.red" ) ).getObject() );
        assertEquals( 60l, parsedContentData.getProperty( DataPath.from( "myColor.green" ) ).getObject() );
        assertEquals( 80l, parsedContentData.getProperty( DataPath.from( "myColor.blue" ) ).getObject() );

        Property myColor = parsedContentData.getProperty( DataPath.from( "myColor" ) );
        Property myColorBlue = myColor.toDataSet().getProperty( "blue" );
        assertEquals( 80l, myColorBlue.getObject() );
    }

    @Test
    public void wholeNumber_within_formItemSet()
        throws IOException
    {
        final FormItemSet formItemSet = newFormItemSet().name( "myFormItemSet" ).build();
        formItemSet.add( newInput().name( "myWholeNumber" ).inputType( InputTypes.WHOLE_NUMBER ).build() );
        final ContentType my_content_type = newContentType().
            module( ModuleName.from( "mymodule" ) ).
            name( "my_content_type" ).
            addFormItem( formItemSet ).
            build();

        StringBuilder json = new StringBuilder();
        json.append( "{" );
        json.append( "\"myFormItemSet.myWholeNumber\": \"1\"" );
        json.append( "}" );
        ObjectMapper objectMapper = ObjectMapperHelper.create();
        ObjectNode objectNode = objectMapper.readValue( json.toString(), ObjectNode.class );

        // exercise
        ContentDataParser contentDataParser = new ContentDataParser( my_content_type );
        DataSet parsedContentData = contentDataParser.parse( objectNode );

        // verify
        assertEquals( 1l, parsedContentData.getProperty( DataPath.from( "myFormItemSet.myWholeNumber" ) ).getObject() );
    }

    @Test
    public void geoLocation()
        throws IOException
    {
        final ContentType contentType = newContentType().
            module( ModuleName.from( "mymodule" ) ).
            name( "my_content_type" ).
            addFormItem( newInput().name( "myGeoLocation" ).inputType( InputTypes.GEO_LOCATION ).build() ).build();

        StringBuilder json = new StringBuilder();
        json.append( "{" ).append( "\n" );
        json.append( "\"myGeoLocation\": \"40.446195,-79.948862\"" ).append( "\n" );
        json.append( "}" );
        ObjectMapper objectMapper = ObjectMapperHelper.create();
        ObjectNode objectNode = objectMapper.readValue( json.toString(), ObjectNode.class );

        // exercise
        ContentDataParser contentDataParser = new ContentDataParser( contentType );
        DataSet parsedContentData = contentDataParser.parse( objectNode );

        // verify

        assertEquals( "40.446195,-79.948862", parsedContentData.getProperty( "myGeoLocation" ).getString() );
        assertEquals( ValueTypes.GEOGRAPHIC_COORDINATE, parsedContentData.getProperty( "myGeoLocation" ).getValueType() );
    }

    @Test
    public void parse_color()
        throws IOException
    {
        final ContentType contentType = newContentType().
            module( ModuleName.from( "mymodule" ) ).
            name( "my_content_type" ).
            addFormItem( newInput().name( "myColor" ).inputType( InputTypes.COLOR ).required( true ).build() ).build();

        StringBuilder json = new StringBuilder();
        json.append( "{" ).append( "\n" );
        json.append( "\"myColor\": \"40;60;80\"" ).append( "\n" );
        json.append( "}" );
        ObjectMapper objectMapper = ObjectMapperHelper.create();
        ObjectNode objectNode = objectMapper.readValue( json.toString(), ObjectNode.class );

        // exercise
        ContentDataParser contentDataParser = new ContentDataParser( contentType );
        DataSet parsedContentData = contentDataParser.parse( objectNode );

        // verify
        assertEquals( "40;60;80", parsedContentData.getProperty( DataPath.from( "myColor" ) ).getObject() );
    }

    @Test
    @Ignore
    public void parse_color_within_formItemSet()
        throws IOException
    {
        final FormItemSet formItemSet = newFormItemSet().name( "myFormItemSet" ).build();
        formItemSet.add( newInput().name( "myColor" ).inputType( InputTypes.COLOR ).required( true ).build() );
        final ContentType contentType = newContentType().
            module( ModuleName.from( "mymodule" ) ).
            name( "my_content_type" ).
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
        ContentDataParser contentDataParser = new ContentDataParser( contentType );
        DataSet parsedDataSet = contentDataParser.parse( objectNode );

        // verify
        assertEquals( 40l, parsedDataSet.getProperty( DataPath.from( "myFormItemSet.myColor.red" ) ).getObject() );
        assertEquals( 60l, parsedDataSet.getProperty( DataPath.from( "myFormItemSet.myColor.green" ) ).getObject() );
        assertEquals( 80l, parsedDataSet.getProperty( DataPath.from( "myFormItemSet.myColor.blue" ) ).getObject() );
    }
}
