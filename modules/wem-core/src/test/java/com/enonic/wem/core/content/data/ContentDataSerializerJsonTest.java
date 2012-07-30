package com.enonic.wem.core.content.data;


import org.junit.Test;

import com.enonic.wem.core.content.type.ContentType;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.content.type.configitem.fieldtype.RadioButtonsConfig;

import static org.junit.Assert.*;

public class ContentDataSerializerJsonTest
{
    @Test
    public void asdfsdf()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "MyContentType" );
        ConfigItems configItems = contentType.getConfigItems();
        configItems.addConfig( Field.newBuilder().name( "myTextarea" ).type( FieldTypes.textarea ).required( true ).build() );
        configItems.addConfig( Field.newBuilder().name( "myPhone" ).type( FieldTypes.phone ).build() );

        ContentData contentData = new ContentData( configItems );
        contentData.setValue( "myTextarea", "My test\n text." );
        contentData.setValue( "myPhone", "+4712123123" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );

        System.out.println( json );

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
        configItems.addConfig( Field.newBuilder().name( "myRadiobuttons" ).type( FieldTypes.radioButtons ).required( true ).fieldTypeConfig(
            radioButtonsConfig ).build() );

        ContentData contentData = new ContentData( configItems );
        contentData.setValue( "myRadiobuttons", "Norway" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );
        System.out.println( json );

        ContentData parsedContentData = generator.parse( json, configItems );

        System.out.println( parsedContentData );
    }

    @Test
    public void multiple_textlines()
    {
        ConfigItems configItems = new ConfigItems();
        configItems.addConfig( Field.newBuilder().name( "myTextLine" ).type( FieldTypes.textline ).build() );
        configItems.addConfig(
            Field.newBuilder().name( "myMultipleTextLine" ).type( FieldTypes.textline ).required( false ).multiple( true ).build() );

        ContentData contentData = new ContentData( configItems );
        contentData.setValue( "myTextLine", "A single line" );
        contentData.setValue( "myMultipleTextLine[0]", "First line" );
        contentData.setValue( "myMultipleTextLine[1]", "Second line" );

        ContentDataSerializerJson serializer = new ContentDataSerializerJson();
        String json = serializer.toJson( contentData );
        System.out.println( json );
        ContentData parsedContentData = serializer.parse( json, configItems );

        assertEquals( "A single line", parsedContentData.getValue( "myTextLine" ).getValue() );
        assertEquals( "First line", parsedContentData.getValue( "myMultipleTextLine[0]" ).getValue() );
        assertEquals( "Second line", parsedContentData.getValue( "myMultipleTextLine[1]" ).getValue() );
    }

    @Test
    public void groupedSubType()
    {
        ConfigItems configItems = new ConfigItems();
        configItems.addConfig( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build() );

        FieldSet.Builder fieldSetBuilder = FieldSet.newBuilder();
        fieldSetBuilder.name( "personalia" );
        fieldSetBuilder.label( "Personalia" );
        FieldSet fieldSet = fieldSetBuilder.build();
        configItems.addConfig( fieldSet );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );

        ContentData contentData = new ContentData( configItems );
        contentData.setValue( "name", "Ola Nordmann" );
        contentData.setValue( "personalia.eyeColour", "Blue" );
        contentData.setValue( "personalia.hairColour", "Blonde" );

        ContentDataSerializerJson serializer = new ContentDataSerializerJson();
        String json = serializer.toJson( contentData );
        System.out.println( json );

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
        configItems.addConfig( nameField );

        FieldSet fieldSet = FieldSet.newBuilder().name( "personalia" ).label( "Personalia" ).multiple( true ).build();
        configItems.addConfig( fieldSet );
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
        //System.out.println(transformer.toJson2( contentData ));
        String json = serializer.toJson( contentData );
        System.out.println( json );

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
        System.out.println( json );

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
        System.out.println( json );

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
        System.out.println( json );

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

        SubTypeEntry companyEntries = (SubTypeEntry) topEntries.getEntry( "company" );
        Value value0 = (Value) companyEntries.getEntries().getEntry( "names[0]" );
        assertEquals( "company.names[0]", value0.getPath().toString() );
        assertEquals( "Thomas", value0.getValue() );
        Value value1 = (Value) companyEntries.getEntries().getEntry( "names[1]" );
        assertEquals( "company.names[1]", value1.getPath().toString() );
        assertEquals( "Sten Roger", value1.getValue() );
        Value value2 = (Value) companyEntries.getEntries().getEntry( "names[2]" );
        assertEquals( "company.names[2]", value2.getPath().toString() );
        assertEquals( "Alex", value2.getValue() );
    }

}
