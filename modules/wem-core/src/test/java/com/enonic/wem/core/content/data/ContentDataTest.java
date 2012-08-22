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
        DropdownConfig dropdownConfig = DropdownConfig.newBuilder().addOption( "Option 1", "o1" ).addOption( "Option 2", "o2" ).build();
        Field myDropdown = Field.newBuilder().name( "myDropdown" ).type( FieldTypes.dropdown ).fieldTypeConfig( dropdownConfig ).build();
        configItems.addConfigItem( myDropdown );

        ContentData contentData = new ContentData( configItems );
        contentData.setData( "myDropdown", "o1" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );
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
        contentData.setData( "myRadioButtons", "c1" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );
    }

    @Test
    public void multiple_textlines()
    {
        ConfigItems configItems = new ConfigItems();
        configItems.addConfigItem( Field.newBuilder().name( "myTextLine" ).type( FieldTypes.textline ).build() );
        configItems.addConfigItem( Field.newBuilder().name( "myMultipleTextLine" ).type( FieldTypes.textline ).multiple( true ).build() );

        ContentData contentData = new ContentData( configItems );
        contentData.setData( "myTextLine", "A single line" );
        contentData.setData( "myMultipleTextLine[0]", "First line" );
        contentData.setData( "myMultipleTextLine[1]", "Second line" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );
        assertEquals( "A single line", contentData.setData( "myTextLine" ).getValue() );
        assertEquals( "First line", contentData.setData( "myMultipleTextLine[0]" ).getValue() );
        assertEquals( "Second line", contentData.setData( "myMultipleTextLine[1]" ).getValue() );
    }

    @Test
    public void tags()
    {
        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addConfigItem( Field.newBuilder().name( "myTags" ).type( FieldTypes.tags ).build() );

        // TODO: Are'nt tags best stored as an array? A global mixin multiple textline?
        ContentData contentData = new ContentData( dataConfig );
        contentData.setData( "myTags", "A line of text" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );
    }

    @Test
    public void phone()
    {
        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addConfigItem( Field.newBuilder().name( "myPhone" ).type( FieldTypes.phone ).required( true ).build() );

        ContentData contentData = new ContentData( dataConfig );
        contentData.setData( "myPhone", "98327891" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );
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
        contentData.setData( "myRadiobuttons", "NO" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );
    }

    @Test
    public void groupedFieldSet()
    {
        ConfigItems dataConfig = new ConfigItems();
        dataConfig.addConfigItem( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build() );

        FieldSet fieldSet = FieldSet.newBuilder().name( "personalia" ).build();
        dataConfig.addConfigItem( fieldSet );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );

        ContentData contentData = new ContentData( dataConfig );
        contentData.setData( "name", "Ola Nordmann" );
        contentData.setData( "personalia.eyeColour", "Blue" );
        contentData.setData( "personalia.hairColour", "blonde" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );
    }

    @Test
    public void multiple_subtype()
    {
        ConfigItems dataConfig = new ConfigItems();
        Field nameField = Field.newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build();
        dataConfig.addConfigItem( nameField );

        FieldSet fieldSet = FieldSet.newBuilder().name( "personalia" ).multiple( true ).build();
        dataConfig.addConfigItem( fieldSet );
        fieldSet.addField( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );

        ContentData contentData = new ContentData( dataConfig );
        contentData.setData( "name", "Norske" );
        contentData.setData( "name", "Norske" );
        contentData.setData( "personalia[0].name", "Ola Nordmann" );
        contentData.setData( "personalia[0].eyeColour", "Blue" );
        contentData.setData( "personalia[0].hairColour", "blonde" );
        contentData.setData( "personalia[1].name", "Kari Trestakk" );
        contentData.setData( "personalia[1].eyeColour", "Green" );
        contentData.setData( "personalia[1].hairColour", "Brown" );

        ContentDataSerializerJson generator = new ContentDataSerializerJson();
        String json = generator.toJson( contentData );
    }

    @Test
    public void unstructured()
    {
        ContentData data = new ContentData();
        data.setData( "firstName", "Thomas" );
        data.setData( "child[0].name", "Joachim" );
        data.setData( "child[0].age", "9" );
        data.setData( "child[0].features.eyeColour", "Blue" );
        data.setData( "child[0].features.hairColour", "Blonde" );
        data.setData( "child[1].name", "Madeleine" );
        data.setData( "child[1].age", "7" );
        data.setData( "child[1].features.eyeColour", "Brown" );
        data.setData( "child[1].features.hairColour", "Black" );

        assertEquals( "Thomas", data.setData( "firstName" ).getValue() );
        assertEquals( "Joachim", data.setData( "child[0].name" ).getValue() );
        assertEquals( "9", data.setData( "child[0].age" ).getValue() );
        assertEquals( "Blue", data.setData( "child[0].features.eyeColour" ).getValue() );
        assertEquals( "Blonde", data.setData( "child[0].features.hairColour" ).getValue() );
        assertEquals( "Madeleine", data.setData( "child[1].name" ).getValue() );
        assertEquals( "7", data.setData( "child[1].age" ).getValue() );
        assertEquals( "Brown", data.setData( "child[1].features.eyeColour" ).getValue() );
        assertEquals( "Black", data.setData( "child[1].features.hairColour" ).getValue() );

        String json = ContentDataSerializerJson.toJson( data );
    }

    @Test
    public void unstructured_getEntries()
    {
        ContentData data = new ContentData();
        data.setData( "child[0].name", "Joachim" );
        data.setData( "child[0].age", "9" );
        data.setData( "child[0].features.eyeColour", "Blue" );
        data.setData( "child[0].features.hairColour", "Blonde" );
        data.setData( "child[1].name", "Madeleine" );
        data.setData( "child[1].age", "7" );
        data.setData( "child[1].features.eyeColour", "Brown" );
        data.setData( "child[1].features.hairColour", "Black" );

        DataSet subTypeEntryChild0 = data.getDataSet( "child[0]" );
        assertEquals( "Joachim", subTypeEntryChild0.getData( "name" ).getValue() );
        assertEquals( "9", subTypeEntryChild0.getData( "age" ).getValue() );
        assertEquals( "Blue", subTypeEntryChild0.getData( "features.eyeColour" ).getValue() );

        DataSet subTypeEntryChild1 = data.getDataSet( "child[1]" );
        assertEquals( "Madeleine", subTypeEntryChild1.getData( "name" ).getValue() );
        assertEquals( "7", subTypeEntryChild1.getData( "age" ).getValue() );
        assertEquals( "Brown", subTypeEntryChild1.getData( "features.eyeColour" ).getValue() );
    }

    @Test
    public void structured_getEntries()
    {
        FieldSet child = FieldSet.newBuilder().name( "child" ).multiple( true ).build();
        child.addField( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).build() );
        child.addField( Field.newBuilder().name( "age" ).type( FieldTypes.textline ).build() );
        FieldSet features = FieldSet.newBuilder().name( "features" ).multiple( false ).build();
        features.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        features.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );
        child.addFieldSet( features );
        ConfigItems configItems = new ConfigItems();
        configItems.addConfigItem( child );

        ContentData data = new ContentData( configItems );
        data.setData( "child[0].name", "Joachim" );
        data.setData( "child[0].age", "9" );
        data.setData( "child[0].features.eyeColour", "Blue" );
        data.setData( "child[0].features.hairColour", "Blonde" );
        data.setData( "child[1].name", "Madeleine" );
        data.setData( "child[1].age", "7" );
        data.setData( "child[1].features.eyeColour", "Brown" );
        data.setData( "child[1].features.hairColour", "Black" );

        DataSet subTypeEntryChild0 = data.getDataSet( "child[0]" );
        assertEquals( "Joachim", subTypeEntryChild0.getData( "name" ).getValue() );
        assertEquals( "9", subTypeEntryChild0.getData( "age" ).getValue() );
        assertEquals( "Blue", subTypeEntryChild0.getData( "features.eyeColour" ).getValue() );

        DataSet subTypeEntryChild1 = data.getDataSet( "child[1]" );
        assertEquals( "Madeleine", subTypeEntryChild1.getData( "name" ).getValue() );
        assertEquals( "7", subTypeEntryChild1.getData( "age" ).getValue() );
        assertEquals( "Brown", subTypeEntryChild1.getData( "features.eyeColour" ).getValue() );
    }
}
