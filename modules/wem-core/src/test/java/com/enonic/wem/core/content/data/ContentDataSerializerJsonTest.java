package com.enonic.wem.core.content.data;


import org.junit.Test;

import com.enonic.wem.core.content.Content;
import com.enonic.wem.core.content.type.ContentType;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.VisualFieldSet;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.content.type.configitem.fieldtype.RadioButtonsConfig;

import static com.enonic.wem.core.content.type.configitem.Field.newField;
import static com.enonic.wem.core.content.type.configitem.FieldSet.newBuilder;
import static com.enonic.wem.core.content.type.configitem.FieldSet.newFieldSet;
import static com.enonic.wem.core.content.type.configitem.VisualFieldSet.newVisualFieldSet;
import static org.junit.Assert.*;

public class ContentDataSerializerJsonTest
{
    /*
    @Test
    public void asdfsdf()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "MyContentType" );
        ConfigItems configItems = contentType.getConfigItems();
        configItems.addConfigItem( Field.newBuilder().name( "myTextarea" ).type( FieldTypes.textarea ).required( true ).build() );
        configItems.addConfigItem( Field.newBuilder().name( "myPhone" ).type( FieldTypes.phone ).build() );

        ContentData contentData = new ContentData( configItems );
        contentData.setData( "myTextarea", "My test\n text." );
        contentData.setData( "myPhone", "+4712123123" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );

        // exercise
        ContentData actualContentData = generator.parse( json, configItems );

        // verify
        assertEquals( 2, actualContentData.getDataSet().size() );
    }

    @Test
    public void radiobuttons()
    {
        RadioButtonsConfig radioButtonsConfig =
            RadioButtonsConfig.newBuilder().addOption( "Norway", "NO" ).addOption( "South Africa", "ZA" ).build();

        ConfigItems configItems = new ConfigItems();
        configItems.addConfigItem(
            Field.newBuilder().name( "myRadiobuttons" ).type( FieldTypes.radioButtons ).required( true ).fieldTypeConfig(
                radioButtonsConfig ).build() );

        ContentData contentData = new ContentData( configItems );
        contentData.setData( "myRadiobuttons", "NO" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );

        ContentData parsedContentData = generator.parse( json, configItems );

    }

    @Test
    public void multiple_textlines()
    {
        ConfigItems configItems = new ConfigItems();
        configItems.addConfigItem( Field.newBuilder().name( "myTextLine" ).type( FieldTypes.textline ).build() );
        configItems.addConfigItem(
            Field.newBuilder().name( "myMultipleTextLine" ).type( FieldTypes.textline ).required( false ).multiple( true ).build() );

        ContentData contentData = new ContentData( configItems );
        contentData.setData( "myTextLine", "A single line" );
        contentData.setData( "myMultipleTextLine[0]", "First line" );
        contentData.setData( "myMultipleTextLine[1]", "Second line" );

        ContentDataSerializerJson serializer = new ContentDataSerializerJson();
        String json = serializer.toJson( contentData );
        ContentData parsedContentData = serializer.parse( json, configItems );

        assertEquals( "A single line", parsedContentData.setData( "myTextLine" ).getValue() );
        assertEquals( "First line", parsedContentData.setData( "myMultipleTextLine[0]" ).getValue() );
        assertEquals( "Second line", parsedContentData.setData( "myMultipleTextLine[1]" ).getValue() );
    }

    @Test
    public void groupedFieldSet()
    {
        ConfigItems configItems = new ConfigItems();
        configItems.addConfigItem( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build() );

        FieldSet fieldSet = newBuilder().name( "personalia" ).build();
        configItems.addConfigItem( fieldSet );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );

        ContentData contentData = new ContentData( configItems );
        contentData.setData( "name", "Ola Nordmann" );
        contentData.setData( "personalia.eyeColour", "Blue" );
        contentData.setData( "personalia.hairColour", "Blonde" );

        ContentDataSerializerJson serializer = new ContentDataSerializerJson();
        String json = serializer.toJson( contentData );

        // exercise
        ContentData actualContentData = serializer.parse( json, configItems );

        // verify
        assertEquals( 2, actualContentData.getDataSet().size() );
        assertEquals( "Blue", actualContentData.setData( "personalia.eyeColour" ).getValue() );
        assertEquals( "Blonde", actualContentData.setData( "personalia.hairColour" ).getValue() );
    }

    @Test
    public void multiple_subtype()
    {
        ConfigItems configItems = new ConfigItems();
        Field nameField = Field.newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build();
        configItems.addConfigItem( nameField );

        FieldSet fieldSet = newFieldSet().name( "personalia" ).label( "Personalia" ).multiple( true ).build();
        configItems.addConfigItem( fieldSet );
        fieldSet.addField( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );

        ContentData contentData = new ContentData( configItems );
        contentData.setData( "name", "Norske" );
        contentData.setData( "personalia[0].name", "Ola Nordmann" );
        contentData.setData( "personalia[0].eyeColour", "Blue" );
        contentData.setData( "personalia[0].hairColour", "Blonde" );
        contentData.setData( "personalia[1].name", "Kari Trestakk" );
        contentData.setData( "personalia[1].eyeColour", "Green" );
        contentData.setData( "personalia[1].hairColour", "Brown" );

        ContentDataSerializerJson serializer = new ContentDataSerializerJson();
        String json = serializer.toJson( contentData );

        // exercise
        ContentData actualContentData = serializer.parse( json, configItems );

        // verify
        assertEquals( 3, actualContentData.getDataSet().size() );
        assertEquals( "Norske", actualContentData.setData( "name" ).getValue() );
        assertEquals( "Ola Nordmann", actualContentData.setData( "personalia[0].name" ).getValue() );
        assertEquals( "Blue", actualContentData.setData( "personalia[0].eyeColour" ).getValue() );
        assertEquals( "Blonde", actualContentData.setData( "personalia[0].hairColour" ).getValue() );
        assertEquals( "Kari Trestakk", actualContentData.setData( "personalia[1].name" ).getValue() );
        assertEquals( "Green", actualContentData.setData( "personalia[1].eyeColour" ).getValue() );
        assertEquals( "Brown", actualContentData.setData( "personalia[1].hairColour" ).getValue() );
    }


    @Test
    public void unstructured_with_subTypes()
    {
        ContentData data = new ContentData();
        data.setData( "name", "Thomas" );
        data.setData( "child[0].name", "Joachim" );
        data.setData( "child[0].age", "9" );
        data.setData( "child[0].features.eyeColour", "Blue" );
        data.setData( "child[0].features.hairColour", "Blonde" );
        data.setData( "child[1].name", "Madeleine" );
        data.setData( "child[1].age", "7" );
        data.setData( "child[1].features.eyeColour", "Brown" );
        data.setData( "child[1].features.hairColour", "Black" );

        String json = ContentDataSerializerJson.toJson( data );

        // exercise
        ContentDataSerializerJson serializer = new ContentDataSerializerJson();
        ContentData actualContentData = serializer.parse( json, null );

        // verify
        DataSet topDataSet = actualContentData.getDataSet();
        assertEquals( 3, topDataSet.size() );
        assertEquals( new EntryPath(), topDataSet.getPath() );
        assertEquals( "Thomas", topDataSet.getData( "name" ).getValue() );
        assertEquals( "Joachim", topDataSet.getData( "child[0].name" ).getValue() );
        assertEquals( "9", topDataSet.getData( "child[0].age" ).getValue() );
        assertEquals( "Blue", topDataSet.getData( "child[0].features.eyeColour" ).getValue() );
        assertEquals( "Blonde", topDataSet.getData( "child[0].features.hairColour" ).getValue() );
        assertEquals( "Madeleine", topDataSet.getData( "child[1].name" ).getValue() );
        assertEquals( "7", topDataSet.getData( "child[1].age" ).getValue() );
        assertEquals( "Brown", topDataSet.getData( "child[1].features.eyeColour" ).getValue() );
        assertEquals( "Black", topDataSet.getData( "child[1].features.hairColour" ).getValue() );
    }

    @Test
    public void unstructured_with_arrays()
    {
        ContentData data = new ContentData();
        data.setData( "names[0]", "Thomas" );
        data.setData( "names[1]", "Sten Roger" );
        data.setData( "names[2]", "Alex" );

        String json = ContentDataSerializerJson.toJson( data );

        // exercise
        ContentDataSerializerJson serializer = new ContentDataSerializerJson();
        ContentData parsedContentData = serializer.parse( json, null );

        // verify
        DataSet topDataSet = parsedContentData.getDataSet();
        assertEquals( 3, topDataSet.size() );
        assertEquals( new EntryPath(), topDataSet.getPath() );
        assertEquals( "Thomas", topDataSet.getData( "names[0]" ).getValue() );
        assertEquals( "Sten Roger", topDataSet.getData( "names[1]" ).getValue() );
        assertEquals( "Alex", topDataSet.getData( "names[2]" ).getValue() );
    }

    @Test
    public void unstructured_with_arrays_within_subType()
    {
        ContentData data = new ContentData();
        data.setData( "company.names[0]", "Thomas" );
        data.setData( "company.names[1]", "Sten Roger" );
        data.setData( "company.names[2]", "Alex" );

        String json = ContentDataSerializerJson.toJson( data );

        // exercise
        ContentDataSerializerJson serializer = new ContentDataSerializerJson();
        ContentData parsedContentData = serializer.parse( json, null );

        // verify
        DataSet topDataSet = parsedContentData.getDataSet();
        assertEquals( 1, topDataSet.size() );
        assertEquals( new EntryPath(), topDataSet.getPath() );
        assertEquals( "Thomas", topDataSet.getData( "company.names[0]" ).getValue() );
        assertEquals( "Sten Roger", topDataSet.getData( "company.names[1]" ).getValue() );
        assertEquals( "Alex", topDataSet.getData( "company.names[2]" ).getValue() );

        DataSet companyDataSet = (DataSet) topDataSet.getEntry( "company" );
        Data data0 = (Data) companyDataSet.getEntry( "names[0]" );
        assertEquals( "company.names[0]", data0.getPath().toString() );
        assertEquals( "Thomas", data0.getValue() );
        Data data1 = (Data) companyDataSet.getEntry( "names[1]" );
        assertEquals( "company.names[1]", data1.getPath().toString() );
        assertEquals( "Sten Roger", data1.getValue() );
        Data data2 = (Data) companyDataSet.getEntry( "names[2]" );
        assertEquals( "company.names[2]", data2.getPath().toString() );
        assertEquals( "Alex", data2.getValue() );
    }

    @Test
    public void visualFieldSet()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        contentType.addConfigItem( newField().name( "myField" ).type( FieldTypes.textline ).build() );
        VisualFieldSet visualFieldSet =
            newVisualFieldSet().label( "Personalia" ).add( newField().name( "eyeColour" ).type( FieldTypes.textline ).build() ).add(
                newField().name( "hairColour" ).type( FieldTypes.textline ).build() ).build();
        contentType.addConfigItem( visualFieldSet );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myField", "myValue" );
        content.setData( "eyeColour", "Blue" );
        content.setData( "hairColour", "Blonde" );

        String json = ContentDataSerializerJson.toJson( content.getData() );
        System.out.println( json );

        // exercise
        ContentDataSerializerJson serializer = new ContentDataSerializerJson();
        ContentData parsedContentData = serializer.parse( json, null );
        assertEquals( "myValue", parsedContentData.getValueAsString( new EntryPath( "myField" ) ) );
        assertEquals( "Blue", parsedContentData.getValueAsString( new EntryPath( "eyeColour" ) ) );
        assertEquals( "Blonde", parsedContentData.getValueAsString( new EntryPath( "hairColour" ) ) );
    }
    */

}
