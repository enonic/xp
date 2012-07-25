package com.enonic.wem.core.content.data;


import org.junit.Test;

import com.enonic.wem.core.content.type.ContentType;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.SubType;
import com.enonic.wem.core.content.type.configitem.field.type.FieldTypes;
import com.enonic.wem.core.content.type.configitem.field.type.RadioButtonsConfig;

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
    public void subType()
    {
        ConfigItems configItems = new ConfigItems();
        configItems.addConfig( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build() );

        SubType.Builder subTypeBuilder = SubType.newBuilder();
        subTypeBuilder.name( "personalia" );
        subTypeBuilder.label( "Personalia" );
        SubType subType = subTypeBuilder.build();
        configItems.addConfig( subType );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );

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
        assertEquals( "Blue", actualContentData.getEntries().getValue( "personalia.eyeColour" ).getValue() );
        assertEquals( "Blonde", actualContentData.getEntries().getValue( "personalia.hairColour" ).getValue() );
    }

    @Test
    public void multiple_subtype()
    {
        ConfigItems configItems = new ConfigItems();
        Field.Builder nameFieldBuilder = Field.newBuilder();
        nameFieldBuilder.name( "name" );
        nameFieldBuilder.type( FieldTypes.textline );
        nameFieldBuilder.required( true );
        Field nameField = nameFieldBuilder.build();
        configItems.addConfig( nameField );

        SubType.Builder subTypeBuilder = SubType.newBuilder();
        subTypeBuilder.name( "personalia" );
        subTypeBuilder.label( "Personalia" );
        subTypeBuilder.multiple( true );
        SubType subType = subTypeBuilder.build();
        configItems.addConfig( subType );
        subType.addField( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );

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
        assertEquals( "Norske", actualContentData.getEntries().getValue( "name" ).getValue() );
        assertEquals( "Ola Nordmann", actualContentData.getEntries().getValue( "personalia[0].name" ).getValue() );
        assertEquals( "Blue", actualContentData.getEntries().getValue( "personalia[0].eyeColour" ).getValue() );
        assertEquals( "Blonde", actualContentData.getEntries().getValue( "personalia[0].hairColour" ).getValue() );
        assertEquals( "Kari Trestakk", actualContentData.getEntries().getValue( "personalia[1].name" ).getValue() );
        assertEquals( "Green", actualContentData.getEntries().getValue( "personalia[1].eyeColour" ).getValue() );
        assertEquals( "Brown", actualContentData.getEntries().getValue( "personalia[1].hairColour" ).getValue() );
    }


    @Test
    public void unstructured()
    {
        ContentData data = new ContentData();
        data.setValue( "firstName", "Thomas" );
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
        assertEquals( new ValuePath(), topEntries.getPath() );
        assertEquals( "Thomas", topEntries.getValue( "firstName" ).getValue() );
        assertEquals( "Joachim", topEntries.getValue( "child[0].name" ).getValue() );
        assertEquals( "9", topEntries.getValue( "child[0].age" ).getValue() );
        assertEquals( "Blue", topEntries.getValue( "child[0].features.eyeColour" ).getValue() );
        assertEquals( "Blonde", topEntries.getValue( "child[0].features.hairColour" ).getValue() );
        assertEquals( "Madeleine", topEntries.getValue( "child[1].name" ).getValue() );
        assertEquals( "7", topEntries.getValue( "child[1].age" ).getValue() );
        assertEquals( "Brown", topEntries.getValue( "child[1].features.eyeColour" ).getValue() );
        assertEquals( "Black", topEntries.getValue( "child[1].features.hairColour" ).getValue() );
    }

}
