package com.enonic.wem.core.content;


import org.junit.Test;

import com.enonic.wem.core.content.data.DataSet;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.ContentType;
import com.enonic.wem.core.content.type.formitem.BreaksRequiredContractException;
import com.enonic.wem.core.content.type.formitem.Component;
import com.enonic.wem.core.content.type.formitem.ComponentTemplate;
import com.enonic.wem.core.content.type.formitem.FormItemSet;
import com.enonic.wem.core.content.type.formitem.FormItemSetTemplate;
import com.enonic.wem.core.content.type.formitem.MockTemplateFetcher;
import com.enonic.wem.core.content.type.formitem.TemplateReference;
import com.enonic.wem.core.content.type.formitem.TemplateType;
import com.enonic.wem.core.content.type.formitem.VisualFieldSet;
import com.enonic.wem.core.content.type.formitem.fieldtype.ComponentTypes;
import com.enonic.wem.core.content.type.formitem.fieldtype.DropdownConfig;
import com.enonic.wem.core.content.type.formitem.fieldtype.RadioButtonsConfig;
import com.enonic.wem.core.module.Module;

import static com.enonic.wem.core.content.type.formitem.Component.newComponent;
import static com.enonic.wem.core.content.type.formitem.ComponentTemplateBuilder.newComponentTemplate;
import static com.enonic.wem.core.content.type.formitem.FormItemSet.newFormItemTest;
import static com.enonic.wem.core.content.type.formitem.FormItemSetTemplateBuilder.newFormItemSetTemplate;
import static com.enonic.wem.core.content.type.formitem.TemplateReference.newTemplateReference;
import static com.enonic.wem.core.content.type.formitem.VisualFieldSet.newVisualFieldSet;
import static com.enonic.wem.core.module.Module.newModule;
import static org.junit.Assert.*;

public class ContentTest
{

    @Test
    public void dropdown()
    {
        ContentType contentType = new ContentType();
        DropdownConfig dropdownConfig = DropdownConfig.newBuilder().addOption( "Option 1", "o1" ).addOption( "Option 2", "o2" ).build();
        Component myDropdown =
            Component.newBuilder().name( "myDropdown" ).type( ComponentTypes.DROPDOWN ).componentTypeConfig( dropdownConfig ).build();
        contentType.addFormItem( myDropdown );

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
        contentType.addFormItem( Component.newBuilder().name( "myRadioButtons" ).type( ComponentTypes.RADIO_BUTTONS ).componentTypeConfig(
            myRadioButtonsConfig ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myRadioButtons", "c1" );

        assertEquals( "c1", content.getData( "myRadioButtons" ).getValue() );
    }

    @Test
    public void multiple_textlines()
    {
        ContentType contentType = new ContentType();
        contentType.addFormItem( Component.newBuilder().name( "myTextLine" ).type( ComponentTypes.TEXT_LINE ).build() );
        contentType.addFormItem(
            Component.newBuilder().name( "myMultipleTextLine" ).type( ComponentTypes.TEXT_LINE ).multiple( true ).build() );

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
        contentType.addFormItem( Component.newBuilder().name( "myTags" ).type( ComponentTypes.TAGS ).build() );

        // TODO: Are'nt tags best stored as an array? A global mixin multiple textline?
        Content content = new Content();
        content.setType( contentType );
        content.setData( "myTags", "A line of text" );

        assertEquals( "A line of text", content.getData( "myTags" ).getValue() );
    }

    @Test
    public void tags_using_fieldTemplate()
    {
        Module module = newModule().name( "system" ).build();
        Component component = newComponent().name( "tags" ).label( "Tags" ).type( ComponentTypes.TEXT_LINE ).multiple( true ).build();
        ComponentTemplate componentTemplate = newComponentTemplate().module( module ).component( component ).build();
        MockTemplateFetcher templateFetcher = new MockTemplateFetcher();
        templateFetcher.add( componentTemplate );

        ContentType contentType = new ContentType();
        contentType.addFormItem(
            TemplateReference.newTemplateReference().name( "myTags" ).template( "system:tags" ).type( TemplateType.COMPONENT ).build() );
        contentType.templateReferencesToFormItems( templateFetcher );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myTags[0]", "Java" );
        content.setData( "myTags[1]", "XML" );
        content.setData( "myTags[2]", "JSON" );

        assertEquals( "Java", content.getData( "myTags" ).getValue() );
    }

    @Test
    public void phone()
    {
        ContentType contentType = new ContentType();
        contentType.addFormItem( Component.newBuilder().name( "myPhone" ).type( ComponentTypes.PHONE ).required( true ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myPhone", "98327891" );

        assertEquals( "98327891", content.getData( "myPhone" ).getValue() );
    }

    @Test
    public void formItemSet()
    {
        ContentType contentType = new ContentType();
        contentType.addFormItem( Component.newBuilder().name( "name" ).type( ComponentTypes.TEXT_LINE ).required( true ).build() );

        FormItemSet formItemSet = FormItemSet.newBuilder().name( "personalia" ).build();
        contentType.addFormItem( formItemSet );
        formItemSet.addItem( newComponent().name( "eyeColour" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItemSet.addItem( newComponent().name( "hairColour" ).type( ComponentTypes.TEXT_LINE ).build() );

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
        Component nameComponent = Component.newBuilder().name( "name" ).type( ComponentTypes.TEXT_LINE ).required( true ).build();
        contentType.addFormItem( nameComponent );

        FormItemSet formItemSet = FormItemSet.newBuilder().name( "personalia" ).multiple( true ).build();
        contentType.addFormItem( formItemSet );
        formItemSet.addItem( Component.newBuilder().name( "name" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItemSet.addItem( Component.newBuilder().name( "eyeColour" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItemSet.addItem( Component.newBuilder().name( "hairColour" ).type( ComponentTypes.TEXT_LINE ).build() );

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
        content.setData( "firstName", "Thomas", DataTypes.STRING );
        content.setData( "description", "Grew up in Noetteveien", DataTypes.HTML_PART );
        content.setData( "child[0].name", "Joachim" );
        content.setData( "child[0].age", "9" );
        content.setData( "child[0].features.eyeColour", "Blue" );
        content.setData( "child[0].features.hairColour", "Blonde" );
        content.setData( "child[1].name", "Madeleine" );
        content.setData( "child[1].age", "7" );
        content.setData( "child[1].features.eyeColour", "Brown" );
        content.setData( "child[1].features.hairColour", "Black" );

        assertEquals( "Thomas", content.getData( "firstName" ).getValue() );
        assertEquals( DataTypes.STRING, content.getData( "firstName" ).getDataType() );
        assertEquals( DataTypes.HTML_PART, content.getData( "description" ).getDataType() );
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
        FormItemSet child = FormItemSet.newBuilder().name( "child" ).multiple( true ).build();
        child.addItem( Component.newBuilder().name( "name" ).type( ComponentTypes.TEXT_LINE ).build() );
        child.addItem( Component.newBuilder().name( "age" ).type( ComponentTypes.TEXT_LINE ).build() );
        FormItemSet features = FormItemSet.newBuilder().name( "features" ).multiple( false ).build();
        features.addItem( Component.newBuilder().name( "eyeColour" ).type( ComponentTypes.TEXT_LINE ).build() );
        features.addItem( Component.newBuilder().name( "hairColour" ).type( ComponentTypes.TEXT_LINE ).build() );
        child.addFormItemSet( features );
        ContentType contentType = new ContentType();
        contentType.addFormItem( child );

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
        content.setData( "personalia.eyeColour", "Blue", DataTypes.STRING );
        content.setData( "personalia.hairColour", "Blonde" );

        assertEquals( DataTypes.STRING, content.getData( "personalia.eyeColour" ).getDataType() );
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

        assertEquals( DataTypes.STRING, content.getData( "personalia.eyeColour" ).getDataType() );
        assertEquals( "Blue", content.getData( "personalia.eyeColour" ).getValue() );
        assertEquals( "personalia.eyeColour", content.getData( "personalia.eyeColour" ).getPath().toString() );

        // exercise
        ContentType contentType = new ContentType();
        contentType.addFormItem( Component.newBuilder().name( "name" ).type( ComponentTypes.TEXT_LINE ).build() );
        contentType.addFormItem( FormItemSet.newBuilder().name( "personalia" ).multiple( false ).build() );
        contentType.getFormItemSet( "personalia" ).addItem(
            Component.newBuilder().name( "eyeColour" ).type( ComponentTypes.TEXT_LINE ).build() );
        contentType.getFormItemSet( "personalia" ).addItem(
            Component.newBuilder().name( "hairColour" ).type( ComponentTypes.TEXT_LINE ).build() );
        contentType.addFormItem( FormItemSet.newBuilder().name( "crimes" ).multiple( true ).build() );
        contentType.getFormItemSet( "crimes" ).addItem(
            Component.newBuilder().name( "description" ).type( ComponentTypes.TEXT_LINE ).build() );
        contentType.getFormItemSet( "crimes" ).addItem( Component.newBuilder().name( "year" ).type( ComponentTypes.TEXT_LINE ).build() );
        content.setType( contentType );

        assertEquals( DataTypes.STRING, content.getData( "personalia.eyeColour" ).getDataType() );
        assertEquals( "Blue", content.getData( "personalia.eyeColour" ).getValue() );
        content.checkValidity();
    }

    @Test
    public void templates()
    {
        Module module = newModule().name( "myModule" ).build();

        ComponentTemplate postalCodeTemplate = newComponentTemplate().module( module ).component(
            Component.newComponent().name( "postalCode" ).type( ComponentTypes.TEXT_LINE ).build() ).build();
        ComponentTemplate countryTemplate = newComponentTemplate().module( module ).component(
            Component.newComponent().name( "country" ).type( ComponentTypes.DROPDOWN ).componentTypeConfig(
                DropdownConfig.newBuilder().addOption( "Norway", "NO" ).build() ).build() ).build();

        FormItemSetTemplate addressTemplate = newFormItemSetTemplate().module( module ).formItemSet(
            newFormItemTest().name( "address" ).add( newComponent().name( "street" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
                newTemplateReference( postalCodeTemplate ).name( "postalCode" ).build() ).add(
                newComponent().name( "postalPlace" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
                newTemplateReference( countryTemplate ).name( "country" ).build() ).build() ).build();

        ContentType contentType = new ContentType();
        contentType.setName( "person" );
        contentType.addFormItem( newComponent().type( ComponentTypes.TEXT_LINE ).name( "name" ).build() );
        contentType.addFormItem( newTemplateReference( addressTemplate ).name( "address" ).build() );

        MockTemplateFetcher templateReferenceFetcher = new MockTemplateFetcher();
        templateReferenceFetcher.add( postalCodeTemplate );
        templateReferenceFetcher.add( countryTemplate );
        templateReferenceFetcher.add( addressTemplate );
        contentType.templateReferencesToFormItems( templateReferenceFetcher );

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

        FormItemSetTemplate addressTemplate = newFormItemSetTemplate().module( module ).formItemSet(
            newFormItemTest().name( "address" ).multiple( true ).add(
                newComponent().type( ComponentTypes.TEXT_LINE ).name( "label" ).build() ).add(
                newComponent().type( ComponentTypes.TEXT_LINE ).name( "street" ).build() ).add(
                newComponent().type( ComponentTypes.TEXT_LINE ).name( "postalCode" ).build() ).add(
                newComponent().type( ComponentTypes.TEXT_LINE ).name( "postalPlace" ).build() ).add(
                newComponent().type( ComponentTypes.TEXT_LINE ).name( "country" ).build() ).build() ).build();

        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        contentType.addFormItem( newTemplateReference( addressTemplate ).name( "address" ).build() );

        MockTemplateFetcher templateReferenceFetcher = new MockTemplateFetcher();
        templateReferenceFetcher.add( addressTemplate );
        contentType.templateReferencesToFormItems( templateReferenceFetcher );

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
        contentType.addFormItem( newComponent().type( ComponentTypes.TEXT_LINE ).name( "name" ).build() );
        contentType.addFormItem( newTemplateReference().name( "address" ).typeField().template( "myModule:myAddressTemplate" ).build() );

        contentType.templateReferencesToFormItems( new MockTemplateFetcher() );

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
            assertEquals( "FormItem at path [address.street] expected to be of type FieldSet: REFERENCE", e.getMessage() );
        }
    }

    @Test
    public void visualFieldSet()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        contentType.addFormItem( newComponent().name( "name" ).type( ComponentTypes.TEXT_LINE ).build() );
        VisualFieldSet personalia = newVisualFieldSet().label( "Personalia" ).name( "personalia" ).add(
            newComponent().name( "eyeColour" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
            newComponent().name( "hairColour" ).type( ComponentTypes.TEXT_LINE ).build() ).build();
        VisualFieldSet tatoos = newVisualFieldSet().label( "Characteristics" ).name( "characteristics" ).add(
            newComponent().name( "tattoo" ).type( ComponentTypes.TEXT_LINE ).multiple( true ).build() ).add(
            newComponent().name( "scar" ).type( ComponentTypes.TEXT_LINE ).multiple( true ).build() ).build();
        personalia.addFormItem( tatoos );
        contentType.addFormItem( personalia );

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

    @Test()
    public void given_required_field_with_data_when_checkBreaksRequiredContract_then_exception_is_not_thrown()
    {
        ContentType contentType = new ContentType();
        contentType.addFormItem( newComponent().name( "myField" ).type( ComponentTypes.TEXT_LINE ).required( true ).build() );
        Content content = new Content();
        content.setType( contentType );
        content.setData( "myField", "value" );

        // exercise
        try
        {
            content.checkBreaksRequiredContract();
        }
        catch ( Exception e )
        {
            fail( "No exception expected" );
        }
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_field_with_no_data_when_checkBreaksRequiredContract_then_exception_is_thrown()
    {

        ContentType contentType = new ContentType();
        contentType.addFormItem( newComponent().name( "myField" ).type( ComponentTypes.TEXT_LINE ).required( true ).build() );
        Content content = new Content();
        content.setType( contentType );

        // exercise
        content.checkBreaksRequiredContract();
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_field_with_no_data_within_visualFieldSet_when_checkBreaksRequiredContract_then_exception_is_thrown()
    {

        ContentType contentType = new ContentType();
        contentType.addFormItem( newVisualFieldSet().label( "My Visual FieldSet" ).name( "myVisualFieldSet" ).add(
            newComponent().name( "myField" ).type( ComponentTypes.TEXT_LINE ).required( true ).build() ).build() );
        Content content = new Content();
        content.setType( contentType );

        // exercise
        content.checkBreaksRequiredContract();
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_field_with_no_data_within_visualFieldSet_within_visualFieldSet_when_checkBreaksRequiredContract_then_exception_is_thrown()
    {

        ContentType contentType = new ContentType();
        contentType.addFormItem( newVisualFieldSet().label( "My outer visual field set" ).name( "myOuterVisualFieldSet" ).add(
            newVisualFieldSet().label( "My Visual FieldSet" ).name( "myVisualFieldSet" ).add(
                newComponent().name( "myField" ).type( ComponentTypes.TEXT_LINE ).required( true ).build() ).build() ).build() );
        Content content = new Content();
        content.setType( contentType );

        // exercise
        content.checkBreaksRequiredContract();
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_field_with_no_data_within_fieldSet_within_visualFieldSet_when_checkBreaksRequiredContract_then_exception_is_thrown()
    {
        ContentType contentType = new ContentType();
        contentType.addFormItem( newVisualFieldSet().label( "My Visual FieldSet" ).name( "myVisualFieldSet" ).add(
            newFormItemTest().name( "myFieldSet" ).required( true ).add(
                newComponent().name( "myField" ).type( ComponentTypes.TEXT_LINE ).required( true ).build() ).build() ).build() );
        Content content = new Content();
        content.setType( contentType );
        content.setData( "myFieldSet.myField", "" );

        // exercise
        content.checkBreaksRequiredContract();
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_field_with_no_data_within_fieldSet_when_checkBreaksRequiredContract_then_exception_is_thrown()
    {

        ContentType contentType = new ContentType();
        contentType.addFormItem( newFormItemTest().name( "myFieldSet" ).required( true ).add(
            newComponent().name( "myField" ).type( ComponentTypes.TEXT_LINE ).required( true ).build() ).build() );
        Content content = new Content();
        content.setType( contentType );
        content.setData( "myFieldSet.myField", "" );

        // exercise
        content.checkBreaksRequiredContract();
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_field_with_no_data_within_visualFieldSet_within_a_fieldSet_when_checkBreaksRequiredContract_then_exception_is_thrown()
    {

        ContentType contentType = new ContentType();
        contentType.addFormItem( newFormItemTest().name( "myFieldSet" ).required( true ).add(
            newVisualFieldSet().label( "My Visual FieldSet" ).name( "myVisualFieldSEt" ).add(
                newComponent().name( "myField" ).type( ComponentTypes.TEXT_LINE ).required( true ).build() ).build() ).build() );
        Content content = new Content();
        content.setType( contentType );
        content.setData( "myFieldSet.myField", "" );

        // exercise
        content.checkBreaksRequiredContract();
    }

    @Test()
    public void given_required_fieldSet_with_data_when_checkBreaksRequiredContract_then_exception_is_not_thrown()
    {

        ContentType contentType = new ContentType();
        contentType.addFormItem( newFormItemTest().name( "myFieldSet" ).required( true ).add(
            newComponent().name( "myField" ).type( ComponentTypes.TEXT_LINE ).build() ).build() );
        Content content = new Content();
        content.setType( contentType );
        content.setData( "myFieldSet.myField", "value" );

        // exercise
        try
        {
            content.checkBreaksRequiredContract();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            fail( "No exception expected: " + e );
        }

    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_fieldSet_with_no_data_when_checkBreaksRequiredContract_then_exception_is_thrown()
    {

        ContentType contentType = new ContentType();
        contentType.addFormItem( newFormItemTest().name( "myFieldSet" ).required( true ).add(
            newComponent().name( "myField" ).type( ComponentTypes.TEXT_LINE ).build() ).build() );
        Content content = new Content();
        content.setType( contentType );

        // exercise
        content.checkBreaksRequiredContract();
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_fieldSet_with_no_data_within_visualFieldSet_when_checkBreaksRequiredContract_then_exception_is_thrown()
    {

        ContentType contentType = new ContentType();
        contentType.addFormItem( newVisualFieldSet().label( "My Visual FieldSet" ).name( "myVisualFieldSet" ).add(
            newFormItemTest().name( "myFieldSet" ).required( true ).add(
                newComponent().name( "myField" ).type( ComponentTypes.TEXT_LINE ).build() ).build() ).build() );
        Content content = new Content();
        content.setType( contentType );

        // exercise
        content.checkBreaksRequiredContract();
    }

    @Test
    public void given_required_fieldSet_with_no_data_and_other_fields_with_data_when_checkBreaksRequiredContract_then_exception_is_thrown()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.addFormItem( newComponent().name( "name" ).type( ComponentTypes.TEXT_LINE ).build() );

        FormItemSet personaliaFormItemSet = newFormItemTest().name( "personalia" ).multiple( false ).required( true ).build();
        personaliaFormItemSet.addItem( newComponent().name( "eyeColour" ).type( ComponentTypes.TEXT_LINE ).build() );
        personaliaFormItemSet.addItem( newComponent().name( "hairColour" ).type( ComponentTypes.TEXT_LINE ).build() );
        contentType.addFormItem( personaliaFormItemSet );

        FormItemSet crimesFormItemSet = newFormItemTest().name( "crimes" ).multiple( true ).build();
        contentType.addFormItem( crimesFormItemSet );
        crimesFormItemSet.addItem( newComponent().name( "description" ).type( ComponentTypes.TEXT_LINE ).build() );
        crimesFormItemSet.addItem( newComponent().name( "year" ).type( ComponentTypes.TEXT_LINE ).build() );

        Content content = new Content();
        content.setType( contentType );

        content.setData( "name", "Thomas" );
        content.setData( "crimes[0].description", "Stole tomatoes from neighbour" );
        content.setData( "crimes[0].year", "1989" );
        content.setData( "crimes[1].description", "Stole a chocolate from the Matbua shop" );
        content.setData( "crimes[1].year", "1990" );

        // exercise
        try
        {
            content.checkBreaksRequiredContract();
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof BreaksRequiredContractException );
            assertEquals( "Required contract is broken, data missing for FormItemSet: personalia", e.getMessage() );
        }
    }
}
