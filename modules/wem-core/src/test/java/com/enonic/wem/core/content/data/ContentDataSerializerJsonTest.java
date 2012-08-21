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
    @Test
    public void asdfsdf()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "MyContentType" );
        ConfigItems configItems = contentType.getConfigItems();
        configItems.addConfigItem( Field.newBuilder().name( "myTextarea" ).type( FieldTypes.textarea ).required( true ).build() );
        configItems.addConfigItem( Field.newBuilder().name( "myPhone" ).type( FieldTypes.phone ).build() );

        ContentData contentData = new ContentData( configItems );
        contentData.setValue( "myTextarea", "My test\n text." );
        contentData.setValue( "myPhone", "+4712123123" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );

        // exercise
        ContentData actualContentData = generator.parse( json, configItems );

        // verify
        assertEquals( 2, actualContentData.getEntries().size() );
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
        contentData.setValue( "myRadiobuttons", "NO" );

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
        contentData.setValue( "myTextLine", "A single line" );
        contentData.setValue( "myMultipleTextLine[0]", "First line" );
        contentData.setValue( "myMultipleTextLine[1]", "Second line" );

        ContentDataSerializerJson serializer = new ContentDataSerializerJson();
        String json = serializer.toJson( contentData );
        ContentData parsedContentData = serializer.parse( json, configItems );

        assertEquals( "A single line", parsedContentData.getValue( "myTextLine" ).getValue() );
        assertEquals( "First line", parsedContentData.getValue( "myMultipleTextLine[0]" ).getValue() );
        assertEquals( "Second line", parsedContentData.getValue( "myMultipleTextLine[1]" ).getValue() );
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
        contentData.setValue( "name", "Ola Nordmann" );
        contentData.setValue( "personalia.eyeColour", "Blue" );
        contentData.setValue( "personalia.hairColour", "Blonde" );

        ContentDataSerializerJson serializer = new ContentDataSerializerJson();
        String json = serializer.toJson( contentData );

        // exercise
        ContentData actualContentData = serializer.parse( json, configItems );

        // verify
        assertEquals( 2, actualContentData.getEntries().size() );
        assertEquals( "Blue", actualContentData.getValue( "personalia.eyeColour" ).getValue() );
        assertEquals( "Blonde", actualContentData.getValue( "personalia.hairColour" ).getValue() );
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
        contentData.setValue( "name", "Norske" );
        contentData.setValue( "personalia[0].name", "Ola Nordmann" );
        contentData.setValue( "personalia[0].eyeColour", "Blue" );
        contentData.setValue( "personalia[0].hairColour", "Blonde" );
        contentData.setValue( "personalia[1].name", "Kari Trestakk" );
        contentData.setValue( "personalia[1].eyeColour", "Green" );
        contentData.setValue( "personalia[1].hairColour", "Brown" );

        ContentDataSerializerJson serializer = new ContentDataSerializerJson();
        String json = serializer.toJson( contentData );

        // exercise
        ContentData actualContentData = serializer.parse( json, configItems );

        // verify
        assertEquals( 3, actualContentData.getEntries().size() );
        assertEquals( "Norske", actualContentData.getValue( "name" ).getValue() );
        assertEquals( "Ola Nordmann", actualContentData.getValue( "personalia[0].name" ).getValue() );
        assertEquals( "Blue", actualContentData.getValue( "personalia[0].eyeColour" ).getValue() );
        assertEquals( "Blonde", actualContentData.getValue( "personalia[0].hairColour" ).getValue() );
        assertEquals( "Kari Trestakk", actualContentData.getValue( "personalia[1].name" ).getValue() );
        assertEquals( "Green", actualContentData.getValue( "personalia[1].eyeColour" ).getValue() );
        assertEquals( "Brown", actualContentData.getValue( "personalia[1].hairColour" ).getValue() );
    }


    @Test
    public void unstructured_with_subTypes()
    {
        ContentData data = new ContentData();
        data.setValue( "name", "Thomas" );
        data.setValue( "child[0].name", "Joachim" );
        data.setValue( "child[0].age", "9" );
        data.setValue( "child[0].features.eyeColour", "Blue" );
        data.setValue( "child[0].features.hairColour", "Blonde" );
        data.setValue( "child[1].name", "Madeleine" );
        data.setValue( "child[1].age", "7" );
        data.setValue( "child[1].features.eyeColour", "Brown" );
        data.setValue( "child[1].features.hairColour", "Black" );

        String json = ContentDataSerializerJson.toJson( data );

        // exercise
        ContentDataSerializerJson serializer = new ContentDataSerializerJson();
        ContentData actualContentData = serializer.parse( json, null );

        // verify
        Entries topEntries = actualContentData.getEntries();
        assertEquals( 3, topEntries.size() );
        assertEquals( new EntryPath(), topEntries.getPath() );
        assertEquals( "Thomas", topEntries.getValue( "name" ).getValue() );
        assertEquals( "Joachim", topEntries.getValue( "child[0].name" ).getValue() );
        assertEquals( "9", topEntries.getValue( "child[0].age" ).getValue() );
        assertEquals( "Blue", topEntries.getValue( "child[0].features.eyeColour" ).getValue() );
        assertEquals( "Blonde", topEntries.getValue( "child[0].features.hairColour" ).getValue() );
        assertEquals( "Madeleine", topEntries.getValue( "child[1].name" ).getValue() );
        assertEquals( "7", topEntries.getValue( "child[1].age" ).getValue() );
        assertEquals( "Brown", topEntries.getValue( "child[1].features.eyeColour" ).getValue() );
        assertEquals( "Black", topEntries.getValue( "child[1].features.hairColour" ).getValue() );
    }

    @Test
    public void unstructured_with_arrays()
    {
        ContentData data = new ContentData();
        data.setValue( "names[0]", "Thomas" );
        data.setValue( "names[1]", "Sten Roger" );
        data.setValue( "names[2]", "Alex" );

        String json = ContentDataSerializerJson.toJson( data );

        // exercise
        ContentDataSerializerJson serializer = new ContentDataSerializerJson();
        ContentData parsedContentData = serializer.parse( json, null );

        // verify
        Entries topEntries = parsedContentData.getEntries();
        assertEquals( 3, topEntries.size() );
        assertEquals( new EntryPath(), topEntries.getPath() );
        assertEquals( "Thomas", topEntries.getValue( "names[0]" ).getValue() );
        assertEquals( "Sten Roger", topEntries.getValue( "names[1]" ).getValue() );
        assertEquals( "Alex", topEntries.getValue( "names[2]" ).getValue() );
    }

    @Test
    public void unstructured_with_arrays_within_subType()
    {
        ContentData data = new ContentData();
        data.setValue( "company.names[0]", "Thomas" );
        data.setValue( "company.names[1]", "Sten Roger" );
        data.setValue( "company.names[2]", "Alex" );

        String json = ContentDataSerializerJson.toJson( data );

        // exercise
        ContentDataSerializerJson serializer = new ContentDataSerializerJson();
        ContentData parsedContentData = serializer.parse( json, null );

        // verify
        Entries topEntries = parsedContentData.getEntries();
        assertEquals( 1, topEntries.size() );
        assertEquals( new EntryPath(), topEntries.getPath() );
        assertEquals( "Thomas", topEntries.getValue( "company.names[0]" ).getValue() );
        assertEquals( "Sten Roger", topEntries.getValue( "company.names[1]" ).getValue() );
        assertEquals( "Alex", topEntries.getValue( "company.names[2]" ).getValue() );

        Entries companyEntries = (Entries) topEntries.getEntry( "company" );
        Value value0 = (Value) companyEntries.getEntry( "names[0]" );
        assertEquals( "company.names[0]", value0.getPath().toString() );
        assertEquals( "Thomas", value0.getValue() );
        Value value1 = (Value) companyEntries.getEntry( "names[1]" );
        assertEquals( "company.names[1]", value1.getPath().toString() );
        assertEquals( "Sten Roger", value1.getValue() );
        Value value2 = (Value) companyEntries.getEntry( "names[2]" );
        assertEquals( "company.names[2]", value2.getPath().toString() );
        assertEquals( "Alex", value2.getValue() );
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
        content.setValue( "myField", "myValue" );
        content.setValue( "eyeColour", "Blue" );
        content.setValue( "hairColour", "Blonde" );

        String json = ContentDataSerializerJson.toJson( content.getData() );
        System.out.println( json );

        // exercise
        ContentDataSerializerJson serializer = new ContentDataSerializerJson();
        ContentData parsedContentData = serializer.parse( json, null );
        assertEquals( "myValue", parsedContentData.getValueAsString( new EntryPath( "myField" ) ) );
        assertEquals( "Blue", parsedContentData.getValueAsString( new EntryPath( "eyeColour" ) ) );
        assertEquals( "Blonde", parsedContentData.getValueAsString( new EntryPath( "hairColour" ) ) );
    }

}
