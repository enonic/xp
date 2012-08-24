package com.enonic.wem.core.content;


import org.junit.Test;

import com.enonic.wem.core.content.data.DataSet;
import com.enonic.wem.core.content.type.ContentType;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.FieldSetTemplate;
import com.enonic.wem.core.content.type.configitem.FieldTemplate;
import com.enonic.wem.core.content.type.configitem.MockTemplateFetcher;
import com.enonic.wem.core.content.type.configitem.VisualFieldSet;
import com.enonic.wem.core.content.type.configitem.fieldtype.DropdownConfig;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.content.type.configitem.fieldtype.RadioButtonsConfig;
import com.enonic.wem.core.content.type.datatype.BasalValueType;
import com.enonic.wem.core.module.Module;

import static com.enonic.wem.core.content.type.configitem.Field.newField;
import static com.enonic.wem.core.content.type.configitem.FieldSet.newFieldSet;
import static com.enonic.wem.core.content.type.configitem.FieldSetTemplateBuilder.newFieldSetTemplate;
import static com.enonic.wem.core.content.type.configitem.FieldTemplateBuilder.newFieldTemplate;
import static com.enonic.wem.core.content.type.configitem.TemplateReference.newTemplateReference;
import static com.enonic.wem.core.content.type.configitem.VisualFieldSet.newVisualFieldSet;
import static com.enonic.wem.core.module.Module.newModule;
import static org.junit.Assert.*;

public class ContentTest
{

    @Test
    public void dropdown()
    {
        ContentType contentType = new ContentType();
        DropdownConfig dropdownConfig = DropdownConfig.newBuilder().addOption( "Option 1", "o1" ).addOption( "Option 2", "o2" ).build();
        Field myDropdown = Field.newBuilder().name( "myDropdown" ).type( FieldTypes.dropdown ).fieldTypeConfig( dropdownConfig ).build();
        contentType.addConfigItem( myDropdown );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myDropdown", "o1" );

        assertEquals( "o1", content.getData( "myDropdown" ).getValue() );
    }

    @Test
    public void radioButtons()
    {
        ContentType contentType = new ContentType();
        RadioButtonsConfig myRadioButtonsConfig =
            RadioButtonsConfig.newBuilder().addOption( "myFirstChoice", "c1" ).addOption( "mySecondChoice", "c2" ).build();
        contentType.addConfigItem(
            Field.newBuilder().name( "myRadioButtons" ).type( FieldTypes.radioButtons ).fieldTypeConfig( myRadioButtonsConfig ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myRadioButtons", "c1" );

        assertEquals( "c1", content.getData( "myRadioButtons" ).getValue() );
    }

    @Test
    public void multiple_textlines()
    {
        ContentType contentType = new ContentType();
        contentType.addConfigItem( Field.newBuilder().name( "myTextLine" ).type( FieldTypes.textline ).build() );
        contentType.addConfigItem( Field.newBuilder().name( "myMultipleTextLine" ).type( FieldTypes.textline ).multiple( true ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myTextLine", "A single line" );
        content.setData( "myMultipleTextLine[0]", "First line" );
        content.setData( "myMultipleTextLine[1]", "Second line" );

        assertEquals( "A single line", content.getData( "myTextLine" ).getValue() );
        assertEquals( "First line", content.getData( "myMultipleTextLine[0]" ).getValue() );
        assertEquals( "Second line", content.getData( "myMultipleTextLine[1]" ).getValue() );
    }

    @Test
    public void tags()
    {
        ContentType contentType = new ContentType();
        contentType.addConfigItem( Field.newBuilder().name( "myTags" ).type( FieldTypes.tags ).build() );

        // TODO: Are'nt tags best stored as an array? A global mixin multiple textline?
        Content content = new Content();
        content.setType( contentType );
        content.setData( "myTags", "A line of text" );

        assertEquals( "A line of text", content.getData( "myTags" ).getValue() );
    }

    @Test
    public void phone()
    {
        ContentType contentType = new ContentType();
        contentType.addConfigItem( Field.newBuilder().name( "myPhone" ).type( FieldTypes.phone ).required( true ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myPhone", "98327891" );

        assertEquals( "98327891", content.getData( "myPhone" ).getValue() );
    }

    @Test
    public void groupedFieldSet()
    {
        ContentType contentType = new ContentType();
        contentType.addConfigItem( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build() );

        FieldSet fieldSet = FieldSet.newBuilder().name( "personalia" ).build();
        contentType.addConfigItem( fieldSet );
        fieldSet.addField( newField().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( newField().name( "hairColour" ).type( FieldTypes.textline ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "name", "Ola Nordmann" );
        content.setData( "personalia.eyeColour", "Blue" );
        content.setData( "personalia.hairColour", "Blonde" );

        assertEquals( "Ola Nordmann", content.getData( "name" ).getValue() );
        assertEquals( "Blue", content.getData( "personalia.eyeColour" ).getValue() );
        assertEquals( "Blonde", content.getData( "personalia.hairColour" ).getValue() );
    }

    @Test
    public void multiple_subtype()
    {
        ContentType contentType = new ContentType();
        Field nameField = Field.newBuilder().name( "name" ).type( FieldTypes.textline ).required( true ).build();
        contentType.addConfigItem( nameField );

        FieldSet fieldSet = FieldSet.newBuilder().name( "personalia" ).multiple( true ).build();
        contentType.addConfigItem( fieldSet );
        fieldSet.addField( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "name", "Norske" );
        content.setData( "personalia[0].name", "Ola Nordmann" );
        content.setData( "personalia[0].eyeColour", "Blue" );
        content.setData( "personalia[0].hairColour", "Blonde" );
        content.setData( "personalia[1].name", "Kari Trestakk" );
        content.setData( "personalia[1].eyeColour", "Green" );
        content.setData( "personalia[1].hairColour", "Brown" );

        assertEquals( "Norske", content.getData( "name" ).getValue() );
        assertEquals( "Ola Nordmann", content.getData( "personalia[0].name" ).getValue() );
        assertEquals( "Blue", content.getData( "personalia[0].eyeColour" ).getValue() );
        assertEquals( "Blonde", content.getData( "personalia[0].hairColour" ).getValue() );
        assertEquals( "Kari Trestakk", content.getData( "personalia[1].name" ).getValue() );
        assertEquals( "Green", content.getData( "personalia[1].eyeColour" ).getValue() );
        assertEquals( "Brown", content.getData( "personalia[1].hairColour" ).getValue() );
    }

    @Test
    public void unstructured()
    {
        Content content = new Content();
        content.setData( "firstName", "Thomas" );
        content.setData( "child[0].name", "Joachim" );
        content.setData( "child[0].age", "9" );
        content.setData( "child[0].features.eyeColour", "Blue" );
        content.setData( "child[0].features.hairColour", "Blonde" );
        content.setData( "child[1].name", "Madeleine" );
        content.setData( "child[1].age", "7" );
        content.setData( "child[1].features.eyeColour", "Brown" );
        content.setData( "child[1].features.hairColour", "Black" );

        assertEquals( "Thomas", content.getData( "firstName" ).getValue() );
        assertEquals( "Joachim", content.getData( "child[0].name" ).getValue() );
        assertEquals( "9", content.getData( "child[0].age" ).getValue() );
        assertEquals( "Blue", content.getData( "child[0].features.eyeColour" ).getValue() );
        assertEquals( "Blonde", content.getData( "child[0].features.hairColour" ).getValue() );
        assertEquals( "Madeleine", content.getData( "child[1].name" ).getValue() );
        assertEquals( "7", content.getData( "child[1].age" ).getValue() );
        assertEquals( "Brown", content.getData( "child[1].features.eyeColour" ).getValue() );
        assertEquals( "Black", content.getData( "child[1].features.hairColour" ).getValue() );
    }

    @Test
    public void unstructured_getEntries()
    {
        Content content = new Content();
        content.setData( "child[0].name", "Joachim" );
        content.setData( "child[0].age", "9" );
        content.setData( "child[0].features.eyeColour", "Blue" );
        content.setData( "child[0].features.hairColour", "Blonde" );
        content.setData( "child[1].name", "Madeleine" );
        content.setData( "child[1].age", "7" );
        content.setData( "child[1].features.eyeColour", "Brown" );
        content.setData( "child[1].features.hairColour", "Black" );

        DataSet child0 = content.getDataSet( "child[0]" );
        assertEquals( "Joachim", child0.getData( "name" ).getValue() );
        assertEquals( "9", child0.getData( "age" ).getValue() );
        assertEquals( "Blue", child0.getData( "features.eyeColour" ).getValue() );

        DataSet child1 = content.getDataSet( "child[1]" );
        assertEquals( "Madeleine", child1.getData( "name" ).getValue() );
        assertEquals( "7", child1.getData( "age" ).getValue() );
        assertEquals( "Brown", child1.getData( "features.eyeColour" ).getValue() );
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
        ContentType contentType = new ContentType();
        contentType.addConfigItem( child );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "child[0].name", "Joachim" );
        content.setData( "child[0].age", "9" );
        content.setData( "child[0].features.eyeColour", "Blue" );
        content.setData( "child[0].features.hairColour", "Blonde" );
        content.setData( "child[1].name", "Madeleine" );
        content.setData( "child[1].age", "7" );
        content.setData( "child[1].features.eyeColour", "Brown" );
        content.setData( "child[1].features.hairColour", "Black" );

        DataSet child0 = content.getDataSet( "child[0]" );
        assertEquals( "Joachim", child0.getData( "name" ).getValue() );
        assertEquals( "9", child0.getData( "age" ).getValue() );
        assertEquals( "Blue", child0.getData( "features.eyeColour" ).getValue() );

        DataSet child1 = content.getDataSet( "child[1]" );
        assertEquals( "Madeleine", child1.getData( "name" ).getValue() );
        assertEquals( "7", child1.getData( "age" ).getValue() );
        assertEquals( "Brown", child1.getData( "features.eyeColour" ).getValue() );
    }

    @Test
    public void given_unstructured_content_when_getting_values_then_they_are_returned()
    {
        // setup
        Content content = new Content();
        content.setData( "name", "Thomas" );
        content.setData( "personalia.eyeColour", "Blue" );
        content.setData( "personalia.hairColour", "Blonde" );

        assertNull( content.getData( "personalia.eyeColour" ).getField() );
        assertEquals( BasalValueType.STRING, content.getData( "personalia.eyeColour" ).getBasalValueType() );
        assertEquals( "Blue", content.getData( "personalia.eyeColour" ).getValue() );
        assertEquals( "personalia.eyeColour", content.getData( "personalia.eyeColour" ).getPath().toString() );
    }

    @Test
    public void given_unstructured_content_when_setting_type_that_fits_then_everything_is_ok()
    {
        // setup
        Content content = new Content();
        content.setData( "name", "Thomas" );
        content.setData( "personalia.eyeColour", "Blue" );
        content.setData( "personalia.hairColour", "Blonde" );
        content.setData( "crimes[0].description", "Stole tomatoes from neighbour" );
        content.setData( "crimes[0].year", "1989" );
        content.setData( "crimes[1].description", "Stole a chocolate from the Matbua shop" );
        content.setData( "crimes[1].year", "1990" );

        assertNull( content.getData( "personalia.eyeColour" ).getField() );
        assertEquals( BasalValueType.STRING, content.getData( "personalia.eyeColour" ).getBasalValueType() );
        assertEquals( "Blue", content.getData( "personalia.eyeColour" ).getValue() );
        assertEquals( "personalia.eyeColour", content.getData( "personalia.eyeColour" ).getPath().toString() );

        // exercise
        ConfigItems configItems = new ConfigItems();
        configItems.addConfigItem( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).build() );
        configItems.addConfigItem( FieldSet.newBuilder().name( "personalia" ).multiple( false ).build() );
        configItems.getFieldSet( "personalia" ).addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        configItems.getFieldSet( "personalia" ).addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );
        configItems.addConfigItem( FieldSet.newBuilder().name( "crimes" ).multiple( true ).build() );
        configItems.getFieldSet( "crimes" ).addField( Field.newBuilder().name( "description" ).type( FieldTypes.textline ).build() );
        configItems.getFieldSet( "crimes" ).addField( Field.newBuilder().name( "year" ).type( FieldTypes.textline ).build() );
        ContentType type = new ContentType();
        type.setConfigItems( configItems );
        content.setType( type );

        assertEquals( BasalValueType.STRING, content.getData( "personalia.eyeColour" ).getBasalValueType() );
        assertEquals( "Blue", content.getData( "personalia.eyeColour" ).getValue() );
        assertEquals( "personalia.eyeColour", content.getData( "personalia.eyeColour" ).getField().getPath().toString() );
        assertEquals( "personalia.hairColour", content.getData( "personalia.hairColour" ).getField().getPath().toString() );
        assertEquals( "crimes.description", content.getData( "crimes[1].description" ).getField().getPath().toString() );
        assertEquals( "crimes.year", content.getData( "crimes[1].year" ).getField().getPath().toString() );
    }

    @Test
    public void templates()
    {
        Module module = newModule().name( "myModule" ).build();

        FieldTemplate postalCodeTemplate =
            newFieldTemplate().module( module ).field( Field.newField().name( "postalCode" ).type( FieldTypes.textline ).build() ).build();
        FieldTemplate countryTemplate = newFieldTemplate().module( module ).field(
            Field.newField().name( "country" ).type( FieldTypes.dropdown ).fieldTypeConfig(
                DropdownConfig.newBuilder().addOption( "Norway", "NO" ).build() ).build() ).build();

        FieldSetTemplate addressTemplate = newFieldSetTemplate().module( module ).fieldSet(
            newFieldSet().name( "address" ).add( newField().name( "street" ).type( FieldTypes.textline ).build() ).add(
                newTemplateReference( postalCodeTemplate ).name( "postalCode" ).build() ).add(
                newField().name( "postalPlace" ).type( FieldTypes.textline ).build() ).add(
                newTemplateReference( countryTemplate ).name( "country" ).build() ).build() ).build();

        ContentType contentType = new ContentType();
        contentType.setName( "person" );
        contentType.addConfigItem( newField().type( FieldTypes.textline ).name( "name" ).build() );
        contentType.addConfigItem( newTemplateReference( addressTemplate ).name( "address" ).build() );

        MockTemplateFetcher templateReferenceFetcher = new MockTemplateFetcher();
        templateReferenceFetcher.add( postalCodeTemplate );
        templateReferenceFetcher.add( countryTemplate );
        templateReferenceFetcher.add( addressTemplate );
        contentType.templateReferencesToConfigItems( templateReferenceFetcher );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "name", "Ola Normann" );
        content.setData( "address.street", "Bakkebygrenda 1" );
        content.setData( "address.postalCode", "2676" );
        content.setData( "address.postalPlace", "Heidal" );
        content.setData( "address.country", "NO" );

        assertEquals( "Ola Normann", content.getValueAsString( "name" ) );
        assertEquals( "Bakkebygrenda 1", content.getValueAsString( "address.street" ) );
        assertEquals( "2676", content.getValueAsString( "address.postalCode" ) );
        assertEquals( "Heidal", content.getValueAsString( "address.postalPlace" ) );
        assertEquals( "NO", content.getValueAsString( "address.country" ) );
    }

    @Test
    public void templates_multiple()
    {
        Module module = newModule().name( "myModule" ).build();

        FieldSetTemplate addressTemplate = newFieldSetTemplate().module( module ).fieldSet(
            newFieldSet().name( "address" ).multiple( true ).add( newField().type( FieldTypes.textline ).name( "label" ).build() ).add(
                newField().type( FieldTypes.textline ).name( "street" ).build() ).add(
                newField().type( FieldTypes.textline ).name( "postalCode" ).build() ).add(
                newField().type( FieldTypes.textline ).name( "postalPlace" ).build() ).add(
                newField().type( FieldTypes.textline ).name( "country" ).build() ).build() ).build();

        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        contentType.addConfigItem( newTemplateReference( addressTemplate ).name( "address" ).build() );

        MockTemplateFetcher templateReferenceFetcher = new MockTemplateFetcher();
        templateReferenceFetcher.add( addressTemplate );
        contentType.templateReferencesToConfigItems( templateReferenceFetcher );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "address[0].label", "Home" );
        content.setData( "address[0].street", "Bakkebygrenda 1" );
        content.setData( "address[0].postalCode", "2676" );
        content.setData( "address[0].postalPlace", "Heidal" );
        content.setData( "address[0].country", "NO" );
        content.setData( "address[1].label", "Cabin" );
        content.setData( "address[1].street", "Heia" );
        content.setData( "address[1].postalCode", "2676" );
        content.setData( "address[1].postalPlace", "Gjende" );
        content.setData( "address[1].country", "NO" );

        assertEquals( "Home", content.getValueAsString( "address[0].label" ) );
        assertEquals( "Bakkebygrenda 1", content.getValueAsString( "address[0].street" ) );
        assertEquals( "2676", content.getValueAsString( "address[0].postalCode" ) );
        assertEquals( "Heidal", content.getValueAsString( "address[0].postalPlace" ) );
        assertEquals( "NO", content.getValueAsString( "address[0].country" ) );

        assertEquals( "Cabin", content.getValueAsString( "address[1].label" ) );
        assertEquals( "Heia", content.getValueAsString( "address[1].street" ) );
        assertEquals( "2676", content.getValueAsString( "address[1].postalCode" ) );
        assertEquals( "Gjende", content.getValueAsString( "address[1].postalPlace" ) );
        assertEquals( "NO", content.getValueAsString( "address[1].country" ) );
    }

    @Test
    public void trying_to_set_data_to_a_fieldSetTemplate_when_template_is_missing()
    {
        ContentType contentType = new ContentType();
        contentType.addConfigItem( newField().type( FieldTypes.textline ).name( "name" ).build() );
        contentType.addConfigItem( newTemplateReference().name( "address" ).typeField().template( "myModule:myAddressTemplate" ).build() );

        contentType.templateReferencesToConfigItems( new MockTemplateFetcher() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "name", "Ola Normann" );
        try
        {
            content.setData( "address.street", "Norvegen 99" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "ConfigItem at path [address.street] expected to be of type FieldSet: REFERENCE", e.getMessage() );
        }
    }

    @Test
    public void required()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.addConfigItem( newField().name( "name" ).type( FieldTypes.textline ).build() );

        FieldSet personaliaFieldSet = newFieldSet().name( "personalia" ).multiple( false ).required( true ).build();
        personaliaFieldSet.addField( newField().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        personaliaFieldSet.addField( newField().name( "hairColour" ).type( FieldTypes.textline ).build() );
        contentType.addConfigItem( personaliaFieldSet );

        FieldSet crimesFieldSet = newFieldSet().name( "crimes" ).multiple( true ).build();
        contentType.addConfigItem( crimesFieldSet );
        crimesFieldSet.addField( newField().name( "description" ).type( FieldTypes.textline ).build() );
        crimesFieldSet.addField( newField().name( "year" ).type( FieldTypes.textline ).build() );

        Content content = new Content();
        content.setType( contentType );

        content.setData( "name", "Thomas" );
        content.setData( "personalia.eyeColour", "Blue" );
        content.setData( "personalia.hairColour", "Blonde" );
        content.setData( "crimes[0].description", "Stole tomatoes from neighbour" );
        content.setData( "crimes[0].year", "1989" );
        content.setData( "crimes[1].description", "Stole a chocolate from the Matbua shop" );
        content.setData( "crimes[1].year", "1990" );

        content.checkBreaksRequiredContract();

        // exercise
        // TODO
    }

    @Test
    public void visualFieldSet()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        contentType.addConfigItem( newField().name( "name" ).type( FieldTypes.textline ).build() );
        VisualFieldSet personalia = newVisualFieldSet().label( "Personalia" ).name( "personalia" ).add(
            newField().name( "eyeColour" ).type( FieldTypes.textline ).build() ).add(
            newField().name( "hairColour" ).type( FieldTypes.textline ).build() ).build();
        VisualFieldSet tatoos = newVisualFieldSet().label( "Characteristics" ).name( "characteristics" ).add(
            newField().name( "tattoo" ).type( FieldTypes.textline ).multiple( true ).build() ).add(
            newField().name( "scar" ).type( FieldTypes.textline ).multiple( true ).build() ).build();
        personalia.addConfigItem( tatoos );
        contentType.addConfigItem( personalia );

        Content content = new Content();
        content.setType( contentType );

        // exercise
        content.setData( "name", "Ola Norman" );
        content.setData( "eyeColour", "Blue" );
        content.setData( "hairColour", "Blonde" );
        content.setData( "tattoo[0]", "Skull on left arm" );
        content.setData( "tattoo[1]", "Mothers name on right arm" );
        content.setData( "scar[0]", "Chin" );

        // verify
        assertEquals( "Ola Norman", content.getValueAsString( "name" ) );
        assertEquals( "Blue", content.getValueAsString( "eyeColour" ) );
        assertEquals( "Blonde", content.getValueAsString( "hairColour" ) );
        assertEquals( "Skull on left arm", content.getValueAsString( "tattoo[0]" ) );
        assertEquals( "Mothers name on right arm", content.getValueAsString( "tattoo[1]" ) );
        assertEquals( "Chin", content.getValueAsString( "scar[0]" ) );
    }
}
