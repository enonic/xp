package com.enonic.wem.core.content;


import org.junit.Test;

import com.enonic.wem.core.content.config.field.ConfigItems;
import com.enonic.wem.core.content.config.field.Field;
import com.enonic.wem.core.content.config.field.RadioButtonsConfig;
import com.enonic.wem.core.content.config.field.SubType;
import com.enonic.wem.core.content.config.field.type.BuiltInFieldTypes;

public class ContentDataTest
{
    @Test
    public void asdfasdf()
    {
        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addField( Field.newBuilder().name( "myTextLine" ).type( BuiltInFieldTypes.textline ).build() );
        dataConfig.addField( Field.newBuilder().name( "myTextArea" ).type( BuiltInFieldTypes.textarea ).build() );
        dataConfig.addField( Field.newBuilder().name( "myDropdown" ).type( BuiltInFieldTypes.dropdown ).build() );
        dataConfig.addField( Field.newBuilder().name( "myPhone" ).type( BuiltInFieldTypes.phone ).required( true ).build() );

        ContentData contentData = new ContentData( dataConfig );
        contentData.setFieldValue( "myTextLine", "A line of text" );
        contentData.setFieldValue( "myTextArea", "A line of text.\nAnother line of text." );
        contentData.setFieldValue( "myDropdown", "V1" );
        contentData.setFieldValue( "myPhone", "98327891" );

        ContentDataJsonGenerator generator = new ContentDataJsonGenerator();
        String json = generator.toJson( contentData );
        System.out.println( json );
    }

    @Test
    public void multiple_textlines()
    {
        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addField( Field.newBuilder().name( "myTextLine" ).type( BuiltInFieldTypes.textline ).build() );
        dataConfig.addField(
            Field.newBuilder().name( "myMultipleTextLine" ).type( BuiltInFieldTypes.textline ).required( false ).multiple( true ).build() );

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
        dataConfig.addField( Field.newBuilder().name( "myTags" ).type( BuiltInFieldTypes.tags ).build() );

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
        dataConfig.addField( Field.newBuilder().name( "myPhone" ).type( BuiltInFieldTypes.phone ).required( true ).build() );

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
        dataConfig.addField(
            Field.newBuilder().name( "myRadiobuttons" ).type( BuiltInFieldTypes.radioButtons ).required( true ).fieldConfig(
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
        dataConfig.addField( Field.newBuilder().name( "name" ).type( BuiltInFieldTypes.textline ).required( true ).build() );

        SubType.Builder subTypeBuilder = SubType.newBuilder();
        subTypeBuilder.name( "personalia" );
        subTypeBuilder.label( "Personalia" );
        SubType subType = subTypeBuilder.build();
        dataConfig.addField( subType );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( BuiltInFieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).type( BuiltInFieldTypes.textline ).build() );

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
        dataConfig.addField( Field.newBuilder().name( "name" ).type( BuiltInFieldTypes.textline ).required( true ).build() );

        SubType.Builder subTypeBuilder = SubType.newBuilder();
        subTypeBuilder.name( "personalia" );
        subTypeBuilder.label( "Personalia" );
        subTypeBuilder.multiple( true );
        SubType subType = subTypeBuilder.build();
        dataConfig.addField( subType );
        subType.addField( Field.newBuilder().name( "name" ).type( BuiltInFieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( BuiltInFieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).type( BuiltInFieldTypes.textline ).build() );

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
