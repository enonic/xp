package com.enonic.wem.core.content;


import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.datatype.InconvertibleValueException;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.form.FieldSet;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.FormItemSetSubType;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.InputSubType;
import com.enonic.wem.api.content.type.form.InvalidDataException;
import com.enonic.wem.api.content.type.form.MockSubTypeFetcher;
import com.enonic.wem.api.content.type.form.SubTypeReference;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.content.type.form.inputtype.SingleSelectorConfig;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.type.ContentType.newContentType;
import static com.enonic.wem.api.content.type.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.FormItemSetSubType.newFormItemSetSubType;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.InputSubType.newInputSubType;
import static com.enonic.wem.api.content.type.form.SubTypeReference.newSubTypeReference;
import static com.enonic.wem.api.content.type.form.inputtype.SingleSelectorConfig.newSingleSelectorConfig;
import static com.enonic.wem.api.module.Module.newModule;
import static org.junit.Assert.*;

public class ContentTest
{

    private ContentType contentType;

    @Before
    public void before()
    {
        contentType = newContentType().
            module( Module.SYSTEM.getName() ).
            name( "MyType" ).
            build();
    }

    @Test
    public void singleSelector()
    {
        SingleSelectorConfig singleSelectorConfig =
            newSingleSelectorConfig().type( SingleSelectorConfig.SelectorType.DROPDOWN ).addOption( "Option 1", "o1" ).addOption(
                "Option 2", "o2" ).build();
        Input mySingleSelector =
            newInput().name( "mySingleSelector" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build();
        contentType.form().addFormItem( mySingleSelector );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "mySingleSelector", "o1" );

        assertEquals( "o1", content.getData( "mySingleSelector" ).getValue() );
    }

    @Test
    public void multiple_textlines()
    {
        contentType.form().addFormItem( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );
        contentType.form().addFormItem( newInput().name( "myMultipleTextLine" ).type( InputTypes.TEXT_LINE ).multiple( true ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "myTextLine", "A single line" );
        content.setData( "myMultipleTextLine[0]", "First line" );
        content.setData( "myMultipleTextLine[1]", "Second line" );

        assertEquals( "A single line", content.getData( "myTextLine" ).getValue() );
        assertEquals( "First line", content.getData( "myMultipleTextLine" ).getDataArray().getData( 0 ).getValue() );
        assertEquals( "Second line", content.getData( "myMultipleTextLine" ).getDataArray().getData( 1 ).getValue() );
    }

    @Test
    public void overwriting_does_not_create_array()
    {
        Content content = newContent().build();
        content.setData( "noArray", "First" );
        content.setData( "noArray", "Second" );
        content.setData( "noArray", "Third" );

        assertEquals( "Third", content.getData( "noArray" ).getValue() );
    }

    @Test
    public void setData_assigning_same_array_element_a_second_time_ovewrites_the_first_value()
    {
        Content content = newContent().build();
        content.setData( "array[0]", "First" );
        content.setData( "array[1]", "Second" );
        content.setData( "array[1]", "Second again" );

        assertEquals( "First", content.getData( "array[0]" ).getValue() );
        assertEquals( "Second again", content.getData( "array[1]" ).getValue() );
        assertNull( content.getData( "array[2]" ) );
    }

    @Test
    public void multiple_textlines2()
    {
        Content content = newContent().build();
        content.setData( "myArray", "First" );
        content.setData( "myArray[1]", "Second" );

        assertEquals( "First", content.getData( "myArray[0]" ).getValue() );
        assertEquals( "Second", content.getData( "myArray[1]" ).getValue() );
        assertEquals( "myArray[0]", content.getData( "myArray[0]" ).getPath().toString() );
        assertEquals( "myArray[1]", content.getData( "myArray[1]" ).getPath().toString() );
        assertEquals( "myArray", content.getData( "myArray" ).getPath().toString() );
    }

    @Test
    public void multiple_textlines3()
    {
        Content content = newContent().build();
        content.setData( "set.myArray[0]", "First" );
        content.setData( "set.myArray[1]", "Second" );

        assertEquals( "First", content.getData( "set.myArray[0]" ).getValue() );
        assertEquals( "Second", content.getData( "set.myArray[1]" ).getValue() );
        assertEquals( "set.myArray[0]", content.getData( "set.myArray[0]" ).getPath().toString() );
        assertEquals( "set.myArray[1]", content.getData( "set.myArray[1]" ).getPath().toString() );
        assertEquals( "set.myArray", content.getData( "set.myArray" ).getPath().toString() );
        assertEquals( "set.myArray[0]", content.getData( "set.myArray" ).getDataArray().getData( 0 ).getPath().toString() );
        assertEquals( "set.myArray[1]", content.getData( "set.myArray" ).getDataArray().getData( 1 ).getPath().toString() );
        assertEquals( "First", content.getData( "set.myArray" ).getDataArray().getData( 0 ).getString() );
        assertEquals( "Second", content.getData( "set.myArray" ).getDataArray().getData( 1 ).getString() );
    }

    @Test
    public void multiple_textlines4()
    {
        Content content = newContent().build();
        content.setData( "set.myArray[0].input", "First" );
        content.setData( "set.myArray[1].input", "Second" );

        assertEquals( "First", content.getData( "set.myArray[0].input" ).getValue() );
        assertEquals( "Second", content.getData( "set.myArray[1].input" ).getValue() );
        assertEquals( "Second", content.getData( "set" ).getDataSet().getData( "myArray" ).getDataArray().getData( 1 ).getDataSet().getData(
            "input" ).getString() );
        assertEquals( "Second", content.getData( "set" ).getDataSet().getData( "myArray[1]" ).getDataSet().getData( "input" ).getString() );
    }

    @Test
    public void array_set()
    {
        Content content = newContent().build();
        content.setData( "set[0].myText", "First" );
        content.setData( "set[1].myText", "Second" );

        assertEquals( "First", content.getData( "set[0].myText" ).getValue() );
        assertEquals( "Second", content.getData( "set[1].myText" ).getValue() );
        assertEquals( "set[0].myText", content.getData( "set[0].myText" ).getPath().toString() );
        assertEquals( "set[1].myText", content.getData( "set[1].myText" ).getPath().toString() );
        assertEquals( "set[0]", content.getData( "set[0]" ).getPath().toString() );
        assertEquals( "set[1]", content.getData( "set[1]" ).getPath().toString() );
        assertEquals( "First", content.getData( "set[0]" ).getDataSet().getData( "myText" ).getString() );
        assertEquals( "Second", content.getData( "set[1]" ).getDataSet().getData( "myText" ).getString() );
    }

    @Test
    public void array_set2()
    {
        Content content = newContent().build();
        content.setData( "set[0].myText", "First" );
        content.setData( "set[0].myOther", "First other" );
        content.setData( "set[1].myText", "Second" );
        content.setData( "set[1].myOther", "Second other" );

        assertEquals( "First", content.getData( "set[0].myText" ).getValue() );
        assertEquals( "First other", content.getData( "set[0].myOther" ).getValue() );
        assertEquals( "Second", content.getData( "set[1].myText" ).getValue() );
        assertEquals( "Second other", content.getData( "set[1].myOther" ).getValue() );
        assertEquals( "set[0].myText", content.getData( "set[0].myText" ).getPath().toString() );
        assertEquals( "set[1].myText", content.getData( "set[1].myText" ).getPath().toString() );
        assertEquals( "set[0]", content.getData( "set[0]" ).getPath().toString() );
        assertEquals( "set[1]", content.getData( "set[1]" ).getPath().toString() );
        assertEquals( "First", content.getData( "set[0]" ).getDataSet().getData( "myText" ).getString() );
        assertEquals( "Second", content.getData( "set[1]" ).getDataSet().getData( "myText" ).getString() );
    }

    @Test
    public void tags()
    {
        contentType.form().addFormItem( newInput().name( "myTags" ).type( InputTypes.TAGS ).build() );

        // TODO: Are'nt tags best stored as an array? A global mixin multiple textline?
        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "myTags", "A line of text" );

        assertEquals( "A line of text", content.getData( "myTags" ).getValue() );
    }

    @Test
    public void tags_using_subType()
    {
        Module module = newModule().name( "system" ).build();
        Input input = newInput().name( "tags" ).label( "Tags" ).type( InputTypes.TEXT_LINE ).multiple( true ).build();
        InputSubType inputSubType = newInputSubType().module( module.getName() ).input( input ).build();
        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( inputSubType );

        contentType.form().addFormItem(
            SubTypeReference.newSubTypeReference().name( "myTags" ).subType( "system:tags" ).type( InputSubType.class ).build() );
        contentType.form().subTypeReferencesToFormItems( subTypeFetcher );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "myTags[0]", "Java" );
        content.setData( "myTags[1]", "XML" );
        content.setData( "myTags[2]", "JSON" );

        Data myTags = content.getData( "myTags" );
        assertEquals( "Java", myTags.getDataArray().getData( 0 ).getString() );
        assertEquals( "XML", myTags.getDataArray().getData( 1 ).getString() );
        assertEquals( "JSON", myTags.getDataArray().getData( 2 ).getString() );
    }

    @Test
    public void phone()
    {
        contentType.form().addFormItem( newInput().name( "myPhone" ).type( InputTypes.PHONE ).required( true ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "myPhone", "98327891" );

        assertEquals( "98327891", content.getData( "myPhone" ).getValue() );
    }

    @Test
    public void formItemSet()
    {
        contentType.form().addFormItem( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).required( true ).build() );

        FormItemSet formItemSet = newFormItemSet().name( "personalia" ).build();
        contentType.form().addFormItem( formItemSet );
        formItemSet.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
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
        Input nameInput = newInput().name( "name" ).type( InputTypes.TEXT_LINE ).required( true ).build();
        contentType.form().addFormItem( nameInput );

        FormItemSet formItemSet = newFormItemSet().name( "personalia" ).multiple( true ).build();
        contentType.form().addFormItem( formItemSet );
        formItemSet.add( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
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
        Content content = newContent().build();
        content.setData( "firstName", "Thomas", DataTypes.TEXT );
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
        assertEquals( DataTypes.TEXT, content.getData( "firstName" ).getDataType() );
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

    @Test(expected = InvalidDataException.class)
    public void setData_given_invalid_value_when_dataType_is_geographical_coordinate_then_exception()
    {
        Content content = newContent().build();
        DataSet value = DataSet.newDataSet().set( "latitude", 0.0, DataTypes.DECIMAL_NUMBER ).set( "longitude", 181.0,
                                                                                                   DataTypes.DECIMAL_NUMBER ).build();
        content.setData( "myGeoLocation", value, DataTypes.GEOGRAPHIC_COORDINATE );
    }

    @Test
    public void unstructured_getEntries()
    {
        Content content = newContent().build();
        content.setData( "child[0].name", "Joachim" );
        content.setData( "child[0].age", "9" );
        content.setData( "child[0].features.eyeColour", "Blue" );
        content.setData( "child[0].features.hairColour", "Blonde" );
        content.setData( "child[1].name", "Madeleine" );
        content.setData( "child[1].age", "7" );
        content.setData( "child[1].features.eyeColour", "Brown" );
        content.setData( "child[1].features.hairColour", "Black" );

        DataSet child0 = content.getData( "child[0]" ).getDataSet();
        assertEquals( "Joachim", child0.getData( "name" ).getValue() );
        assertEquals( "9", child0.getData( "age" ).getValue() );
        assertEquals( "Blue", child0.getData( "features.eyeColour" ).getValue() );

        DataSet child1 = content.getData( "child[1]" ).getDataSet();
        assertEquals( "Madeleine", child1.getData( "name" ).getValue() );
        assertEquals( "7", child1.getData( "age" ).getValue() );
        assertEquals( "Brown", child1.getData( "features.eyeColour" ).getValue() );
    }

    @Test
    public void structured_getEntries()
    {
        FormItemSet child = newFormItemSet().name( "child" ).multiple( true ).build();
        child.add( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() );
        child.add( newInput().name( "age" ).type( InputTypes.TEXT_LINE ).build() );
        FormItemSet features = newFormItemSet().name( "features" ).multiple( false ).build();
        features.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        features.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );
        child.add( features );
        contentType.form().addFormItem( child );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "child[0].name", "Joachim" );
        content.setData( "child[0].age", "9" );
        content.setData( "child[0].features.eyeColour", "Blue" );
        content.setData( "child[0].features.hairColour", "Blonde" );
        content.setData( "child[1].name", "Madeleine" );
        content.setData( "child[1].age", "7" );
        content.setData( "child[1].features.eyeColour", "Brown" );
        content.setData( "child[1].features.hairColour", "Black" );

        DataSet child0 = content.getData( "child[0]" ).getDataSet();
        assertEquals( "Joachim", child0.getData( "name" ).getValue() );
        assertEquals( "9", child0.getData( "age" ).getValue() );
        assertEquals( "Blue", child0.getData( "features.eyeColour" ).getValue() );

        DataSet child1 = content.getData( "child[1]" ).getDataSet();
        assertEquals( "Madeleine", child1.getData( "name" ).getValue() );
        assertEquals( "7", child1.getData( "age" ).getValue() );
        assertEquals( "Brown", child1.getData( "features.eyeColour" ).getValue() );
    }

    @Test
    public void given_unstructured_content_when_getting_values_then_they_are_returned()
    {
        // setup
        Content content = newContent().build();
        content.setData( "name", "Thomas" );
        content.setData( "personalia.eyeColour", "Blue", DataTypes.TEXT );
        content.setData( "personalia.hairColour", "Blonde" );

        assertEquals( DataTypes.TEXT, content.getData( "personalia.eyeColour" ).getDataType() );
        assertEquals( "Blue", content.getData( "personalia.eyeColour" ).getValue() );
        assertEquals( "personalia.eyeColour", content.getData( "personalia.eyeColour" ).getPath().toString() );
    }

    @Test
    public void subTypes()
    {
        Module module = newModule().name( "myModule" ).build();

        InputSubType postalCodeSubType = newInputSubType().module( module.getName() ).input(
            newInput().name( "postalCode" ).type( InputTypes.TEXT_LINE ).build() ).build();
        InputSubType countrySubType = newInputSubType().module( module.getName() ).input(
            newInput().name( "country" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig(
                newSingleSelectorConfig().typeDropdown().addOption( "Norway", "NO" ).build() ).build() ).build();

        FormItemSetSubType addressSubType = newFormItemSetSubType().module( module.getName() ).formItemSet(
            newFormItemSet().name( "address" ).add( newInput().name( "street" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newSubTypeReference( postalCodeSubType ).name( "postalCode" ).build() ).add(
                newInput().name( "postalPlace" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newSubTypeReference( countrySubType ).name( "country" ).build() ).build() ).build();

        contentType.form().addFormItem( newInput().type( InputTypes.TEXT_LINE ).name( "name" ).build() );
        contentType.form().addFormItem( newSubTypeReference( addressSubType ).name( "address" ).build() );

        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( postalCodeSubType );
        subTypeFetcher.add( countrySubType );
        subTypeFetcher.add( addressSubType );
        contentType.form().subTypeReferencesToFormItems( subTypeFetcher );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
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
    public void subTypes_multiple()
    {
        Module module = newModule().name( "myModule" ).build();

        FormItemSetSubType addressSubType = newFormItemSetSubType().module( module.getName() ).formItemSet(
            newFormItemSet().name( "address" ).multiple( true ).add( newInput().type( InputTypes.TEXT_LINE ).name( "label" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "street" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "postalCode" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "postalPlace" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "country" ).build() ).build() ).build();

        contentType.form().addFormItem( newSubTypeReference( addressSubType ).name( "address" ).build() );

        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( addressSubType );
        contentType.form().subTypeReferencesToFormItems( subTypeFetcher );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
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
    public void trying_to_set_data_to_a_fieldSetSubType_when_subType_is_missing()
    {
        contentType.form().addFormItem( newInput().type( InputTypes.TEXT_LINE ).name( "name" ).build() );
        contentType.form().addFormItem(
            SubTypeReference.newSubTypeReference().name( "address" ).typeInput().subType( "myModule:myAddressSubType" ).build() );

        contentType.form().subTypeReferencesToFormItems( new MockSubTypeFetcher() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
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
    public void layout()
    {
        // setup
        contentType.form().addFormItem( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() );
        FieldSet personalia = newFieldSet().label( "Personalia" ).name( "personalia" ).add(
            newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() ).add(
            newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() ).build();
        FieldSet tatoos = newFieldSet().label( "Characteristics" ).name( "characteristics" ).add(
            newInput().name( "tattoo" ).type( InputTypes.TEXT_LINE ).multiple( true ).build() ).add(
            newInput().name( "scar" ).type( InputTypes.TEXT_LINE ).multiple( true ).build() ).build();
        personalia.addFormItem( tatoos );
        contentType.form().addFormItem( personalia );

        Content content = newContent().type( contentType.getQualifiedName() ).build();

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

    @Test(expected = InconvertibleValueException.class)
    public void given_array_when_setting_data_of_another_type_to_array_then_exception_is_thrown()
    {
        // setup
        Content content = newContent().build();
        content.setData( "myData", "Value 1", DataTypes.TEXT );

        // exercise
        content.setData( "myData[1]", new DateMidnight( 2000, 1, 1 ), DataTypes.DATE );
    }
}
