package com.enonic.wem.core.content;


import org.junit.Test;

import com.enonic.wem.core.content.config.field.ConfigItems;
import com.enonic.wem.core.content.config.field.Field;
import com.enonic.wem.core.content.config.field.SubType;
import com.enonic.wem.core.content.config.field.type.DropdownConfig;
import com.enonic.wem.core.content.config.field.type.FieldTypes;
import com.enonic.wem.core.content.config.field.type.RadioButtonsConfig;

public class ContentDataTest
{
    @Test
    public void dropdown()
    {
        ConfigItems dataConfig = new ConfigItems();
        DropdownConfig dropdownConfig = DropdownConfig.newBuilder().addOption( "o1", "Option 1" ).build();
        dataConfig.addField(
            Field.newBuilder().name( "myDropdown" ).type( FieldTypes.dropdown ).fieldTypeConfig( dropdownConfig ).build() );

        ContentData contentData = new ContentData( dataConfig );
        contentData.setFieldValue( "myDropdown", "V1" );

        ContentDataJsonGenerator generator = new ContentDataJsonGenerator();
        String json = generator.toJson( contentData );
        System.out.println( json );
    }

    @Test
    public void radioButtons()
    {
        ConfigItems dataConfig = new ConfigItems();
        RadioButtonsConfig myRadioButtonsConfig =
            RadioButtonsConfig.newBuilder().addOption( "myFirstChoice", "c1" ).addOption( "mySecondChoice", "c2" ).build();
        dataConfig.addField(
            Field.newBuilder().name( "myRadioButtons" ).type( FieldTypes.radioButtons ).fieldTypeConfig( myRadioButtonsConfig ).build() );

        ContentData contentData = new ContentData( dataConfig );
        contentData.setFieldValue( "myRadioButtons", "V1" );

        ContentDataJsonGenerator generator = new ContentDataJsonGenerator();
        String json = generator.toJson( contentData );
        System.out.println( json );
    }

    @Test
    public void multiple_textlines()
    {
        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addField( Field.newBuilder().name( "myTextLine" ).type( FieldTypes.textline ).build() );
        dataConfig.addField(
            Field.newBuilder().name( "myMultipleTextLine" ).type( FieldTypes.textline ).required( false ).multiple( true ).build() );

        ContentData contentData = new ContentData( dataConfig );
        contentData.setFieldValue( "myTextLine", "A single line" );
        contentData.setFieldValue( "myMultipleTextLine[0]", "First line" );
        contentData.setFieldValue( "myMultipleTextLine[1]", "Second line" );

        ContentDataJsonGenerator generator = new ContentDataJsonGenerator();
        String json = generator.toJson( contentData );
        System.out.println( json );
    }

    @Test
    public void tags()
    {
        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addField( Field.newBuilder().name( "myTags" ).type( FieldTypes.tags ).build() );

        // TODO: Are'nt tags best stored as an array? A global mixin multiple textline?
        ContentData contentData = new ContentData( dataConfig );
        contentData.setFieldValue( "myTags", "A line of text" );

        ContentDataJsonGenerator generator = new ContentDataJsonGenerator();
        String json = generator.toJson( contentData );
        System.out.println( json );
    }

    @Test
    public void phone()
    {
        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addField( Field.newBuilder().name( "myPhone" ).type( FieldTypes.phone ).required( true ).build() );

        ContentData contentData = new ContentData( dataConfig );
        contentData.setFieldValue( "myPhone", "98327891" );

        ContentDataJsonGenerator generator = new ContentDataJsonGenerator();
        String json = generator.toJson( contentData );
        System.out.println( json );
    }

    @Test
    public void radiobuttons()
    {
        RadioButtonsConfig radioButtonsConfig =
            RadioButtonsConfig.newBuilder().addOption( "Norway", "NO" ).addOption( "South Africa", "ZA" ).build();

        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addField( Field.newBuilder().name( "myRadiobuttons" ).type( FieldTypes.radioButtons ).required( true ).fieldTypeConfig(
            radioButtonsConfig ).build() );

        ContentData contentData = new ContentData( dataConfig );
        contentData.setFieldValue( "myRadiobuttons", "Norway" );

        ContentDataJsonGenerator generator = new ContentDataJsonGenerator();
        String json = generator.toJson( contentData );
        System.out.println( json );
    }

    @Test
    public void subtype()
    {
        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addField( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build() );

        SubType.Builder subTypeBuilder = SubType.newBuilder();
        subTypeBuilder.name( "personalia" );
        subTypeBuilder.label( "Personalia" );
        SubType subType = subTypeBuilder.build();
        dataConfig.addField( subType );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );

        ContentData contentData = new ContentData( dataConfig );
        contentData.setFieldValue( "name", "Ola Nordmann" );
        contentData.setFieldValue( "personalia.eyeColour", "Blue" );
        contentData.setFieldValue( "personalia.hairColour", "blonde" );

        ContentDataJsonGenerator generator = new ContentDataJsonGenerator();
        String json = generator.toJson( contentData );
        System.out.println( json );
    }

    @Test
    public void multiple_subtype()
    {
        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addField( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build() );

        SubType.Builder subTypeBuilder = SubType.newBuilder();
        subTypeBuilder.name( "personalia" );
        subTypeBuilder.label( "Personalia" );
        subTypeBuilder.multiple( true );
        SubType subType = subTypeBuilder.build();
        dataConfig.addField( subType );
        subType.addField( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );

        ContentData contentData = new ContentData( dataConfig );
        contentData.setFieldValue( "name", "Norske" );
        contentData.setFieldValue( "personalia[0].name", "Ola Nordmann" );
        contentData.setFieldValue( "personalia[0].eyeColour", "Blue" );
        contentData.setFieldValue( "personalia[0].hairColour", "blonde" );
        contentData.setFieldValue( "personalia[1].name", "Kari Trestakk" );
        contentData.setFieldValue( "personalia[1].eyeColour", "Green" );
        contentData.setFieldValue( "personalia[1].hairColour", "Brown" );

        ContentDataJsonGenerator generator = new ContentDataJsonGenerator();
        //System.out.println(transformer.toJson2( contentData ));
        String json = generator.toJson( contentData );
        System.out.println( json );
    }
}
