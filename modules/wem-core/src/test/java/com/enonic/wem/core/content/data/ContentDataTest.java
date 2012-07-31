package com.enonic.wem.core.content.data;


import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.fieldtype.DropdownConfig;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.content.type.configitem.fieldtype.RadioButtonsConfig;

import static org.junit.Assert.*;

public class ContentDataTest
{
    @Test
    public void dropdown()
    {
        ConfigItems configItems = new ConfigItems();
        DropdownConfig dropdownConfig = DropdownConfig.newBuilder().addOption( "o1", "Option 1" ).addOption( "o2", "Option 2" ).build();
        Field myDropdown = Field.newBuilder().name( "myDropdown" ).type( FieldTypes.dropdown ).fieldTypeConfig( dropdownConfig ).build();
        configItems.addConfigItem( myDropdown );

        ContentData contentData = new ContentData( configItems );
        contentData.setValue( "myDropdown", "o1" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );
        System.out.println( json );
    }

    @Test
    public void radioButtons()
    {
        ConfigItems dataConfig = new ConfigItems();
        RadioButtonsConfig myRadioButtonsConfig =
            RadioButtonsConfig.newBuilder().addOption( "myFirstChoice", "c1" ).addOption( "mySecondChoice", "c2" ).build();
        dataConfig.addConfigItem(
            Field.newBuilder().name( "myRadioButtons" ).type( FieldTypes.radioButtons ).fieldTypeConfig( myRadioButtonsConfig ).build() );

        ContentData contentData = new ContentData( dataConfig );
        contentData.setValue( "myRadioButtons", "V1" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );
        System.out.println( json );
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

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );
        System.out.println( json );
        assertEquals( "A single line", contentData.getValue( "myTextLine" ).getValue() );
        assertEquals( "First line", contentData.getValue( "myMultipleTextLine[0]" ).getValue() );
        assertEquals( "Second line", contentData.getValue( "myMultipleTextLine[1]" ).getValue() );
    }

    @Test
    public void tags()
    {
        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addConfigItem( Field.newBuilder().name( "myTags" ).type( FieldTypes.tags ).build() );

        // TODO: Are'nt tags best stored as an array? A global mixin multiple textline?
        ContentData contentData = new ContentData( dataConfig );
        contentData.setValue( "myTags", "A line of text" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );
        System.out.println( json );
    }

    @Test
    public void phone()
    {
        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addConfigItem( Field.newBuilder().name( "myPhone" ).type( FieldTypes.phone ).required( true ).build() );

        ContentData contentData = new ContentData( dataConfig );
        contentData.setValue( "myPhone", "98327891" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );
        System.out.println( json );
    }

    @Test
    public void radiobuttons()
    {
        RadioButtonsConfig radioButtonsConfig =
            RadioButtonsConfig.newBuilder().addOption( "Norway", "NO" ).addOption( "South Africa", "ZA" ).build();

        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addConfigItem(
            Field.newBuilder().name( "myRadiobuttons" ).type( FieldTypes.radioButtons ).required( true ).fieldTypeConfig(
                radioButtonsConfig ).build() );

        ContentData contentData = new ContentData( dataConfig );
        contentData.setValue( "myRadiobuttons", "Norway" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );
        System.out.println( json );
    }

    @Test
    public void groupedFieldSet()
    {
        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addConfigItem( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build() );

        FieldSet fieldSet = FieldSet.newBuilder().typeGroup().name( "personalia" ).build();
        dataConfig.addConfigItem( fieldSet );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );

        ContentData contentData = new ContentData( dataConfig );
        contentData.setValue( "name", "Ola Nordmann" );
        contentData.setValue( "personalia.eyeColour", "Blue" );
        contentData.setValue( "personalia.hairColour", "blonde" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );
        System.out.println( json );
    }

    @Test
    public void multiple_subtype()
    {
        ConfigItems dataConfig = new ConfigItems();
        Field nameField = Field.newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build();
        dataConfig.addConfigItem( nameField );

        FieldSet fieldSet = FieldSet.newBuilder().typeGroup().name( "personalia" ).multiple( true ).build();
        dataConfig.addConfigItem( fieldSet );
        fieldSet.addField( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );

        ContentData contentData = new ContentData( dataConfig );
        contentData.setValue( "name", "Norske" );
        contentData.setValue( "name", "Norske" );
        contentData.setValue( "personalia[0].name", "Ola Nordmann" );
        contentData.setValue( "personalia[0].eyeColour", "Blue" );
        contentData.setValue( "personalia[0].hairColour", "blonde" );
        contentData.setValue( "personalia[1].name", "Kari Trestakk" );
        contentData.setValue( "personalia[1].eyeColour", "Green" );
        contentData.setValue( "personalia[1].hairColour", "Brown" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        //System.out.println(transformer.toJson2( contentData ));
        String json = generator.toJson( contentData );
        System.out.println( json );
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

        assertEquals( "Thomas", data.getValue( "firstName" ).getValue() );
        assertEquals( "Joachim", data.getValue( "child[0].name" ).getValue() );
        assertEquals( "9", data.getValue( "child[0].age" ).getValue() );
        assertEquals( "Blue", data.getValue( "child[0].features.eyeColour" ).getValue() );
        assertEquals( "Blonde", data.getValue( "child[0].features.hairColour" ).getValue() );
        assertEquals( "Madeleine", data.getValue( "child[1].name" ).getValue() );
        assertEquals( "7", data.getValue( "child[1].age" ).getValue() );
        assertEquals( "Brown", data.getValue( "child[1].features.eyeColour" ).getValue() );
        assertEquals( "Black", data.getValue( "child[1].features.hairColour" ).getValue() );

        String json = ContentDataSerializerJson.toJson( data );
        System.out.println( json );
    }

    @Test
    public void unstructured_getEntries()
    {
        ContentData data = new ContentData();
        data.setValue( "child[0].name", "Joachim" );
        data.setValue( "child[0].age", "9" );
        data.setValue( "child[0].features.eyeColour", "Blue" );
        data.setValue( "child[0].features.hairColour", "Blonde" );
        data.setValue( "child[1].name", "Madeleine" );
        data.setValue( "child[1].age", "7" );
        data.setValue( "child[1].features.eyeColour", "Brown" );
        data.setValue( "child[1].features.hairColour", "Black" );

        Entries subTypeEntryChild0 = data.getEntries( "child[0]" );
        assertEquals( "Joachim", subTypeEntryChild0.getValue( "name" ).getValue() );
        assertEquals( "9", subTypeEntryChild0.getValue( "age" ).getValue() );
        assertEquals( "Blue", subTypeEntryChild0.getValue( "features.eyeColour" ).getValue() );

        Entries subTypeEntryChild1 = data.getEntries( "child[1]" );
        assertEquals( "Madeleine", subTypeEntryChild1.getValue( "name" ).getValue() );
        assertEquals( "7", subTypeEntryChild1.getValue( "age" ).getValue() );
        assertEquals( "Brown", subTypeEntryChild1.getValue( "features.eyeColour" ).getValue() );
    }

    @Test
    public void structured_getEntries()
    {
        FieldSet child = FieldSet.newBuilder().typeGroup().name( "child" ).multiple( true ).build();
        child.addField( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).build() );
        child.addField( Field.newBuilder().name( "age" ).type( FieldTypes.textline ).build() );
        FieldSet features = FieldSet.newBuilder().typeGroup().name( "features" ).multiple( false ).build();
        features.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        features.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );
        child.addFieldSet( features );
        ConfigItems configItems = new ConfigItems();
        configItems.addConfigItem( child );

        ContentData data = new ContentData( configItems );
        data.setValue( "child[0].name", "Joachim" );
        data.setValue( "child[0].age", "9" );
        data.setValue( "child[0].features.eyeColour", "Blue" );
        data.setValue( "child[0].features.hairColour", "Blonde" );
        data.setValue( "child[1].name", "Madeleine" );
        data.setValue( "child[1].age", "7" );
        data.setValue( "child[1].features.eyeColour", "Brown" );
        data.setValue( "child[1].features.hairColour", "Black" );

        Entries subTypeEntryChild0 = data.getEntries( "child[0]" );
        assertEquals( "Joachim", subTypeEntryChild0.getValue( "name" ).getValue() );
        assertEquals( "9", subTypeEntryChild0.getValue( "age" ).getValue() );
        assertEquals( "Blue", subTypeEntryChild0.getValue( "features.eyeColour" ).getValue() );

        Entries subTypeEntryChild1 = data.getEntries( "child[1]" );
        assertEquals( "Madeleine", subTypeEntryChild1.getValue( "name" ).getValue() );
        assertEquals( "7", subTypeEntryChild1.getValue( "age" ).getValue() );
        assertEquals( "Brown", subTypeEntryChild1.getValue( "features.eyeColour" ).getValue() );
    }
}
