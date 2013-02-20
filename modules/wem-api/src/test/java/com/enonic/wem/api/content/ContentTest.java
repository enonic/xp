package com.enonic.wem.api.content;


import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.Text;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.content.schema.mixin.MockMixinFetcher;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.form.FieldSet;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.data.Data.newData;
import static com.enonic.wem.api.content.data.Data.newText;
import static com.enonic.wem.api.content.data.Data.newXml;
import static com.enonic.wem.api.content.data.DataSet.newDataSet;
import static com.enonic.wem.api.content.schema.mixin.Mixin.newMixin;
import static com.enonic.wem.api.content.type.ContentType.newContentType;
import static com.enonic.wem.api.content.type.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.MixinReference.newMixinReference;
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
    public void array_getting_entry_from_array_of_size_one()
    {
        Content content = newContent().build();
        content.setData( "array[0]", "First" );

        assertEquals( "First", content.getData( "array" ).getObject() );
        assertEquals( "First", content.getData( "array[0]" ).getObject() );
    }

    @Test
    public void array_getting_entries_from_array_of_size_two()
    {
        Content content = newContent().build();
        content.setData( "array[0]", "First" );
        content.setData( "array[1]", "Second" );

        Data array = content.getData( "array" );
        assertEquals( "First", array.getObject() );
        assertEquals( "First", content.getData( "array" ).asString( 0 ) );
        assertEquals( "First", content.getData( "array[0]" ).asString() );

        assertEquals( "Second", content.getData( "array" ).asString( 1 ) );
        assertEquals( "Second", content.getData( "array[1]" ).asString() );
    }

    @Test
    public void array()
    {
        Content content = newContent().build();
        Data first = newData().name( "array" ).type( DataTypes.TEXT ).value( "First" ).build();
        Data second = newData().name( "array" ).type( DataTypes.TEXT ).value( "Second" ).build();
        content.getRootDataSet().add( first );
        content.getRootDataSet().add( second );

        Data array = content.getData( "array" );
        assertEquals( "First", array.getObject() );
        assertEquals( "First", content.getData( "array" ).asString( 0 ) );
        assertEquals( "First", content.getData( "array[0]" ).getObject() );

        assertEquals( "Second", content.getData( "array" ).asString( 1 ) );
        assertEquals( "Second", content.getData( "array[1]" ).asString() );

    }

    @Test
    public void array_getting_entries_from_array_of_size_three()
    {
        Content content = newContent().build();
        content.setData( "array[0]", "First" );
        content.setData( "array[1]", "Second" );
        content.setData( "array[2]", "Third" );

        assertEquals( "First", content.getData( "array" ).getObject() );
        assertEquals( "First", content.getData( "array[0]" ).getObject() );

        assertEquals( "Second", content.getData( "array[1]" ).getObject() );

        assertEquals( "Third", content.getData( "array[2]" ).getObject() );
    }

    @Test
    public void array_overwriting_does_not_create_array()
    {
        Content content = newContent().build();
        content.setData( "noArray", "First" );
        content.setData( "noArray", "Second" );
        content.setData( "noArray", "Third" );

        assertEquals( "Third", content.getData( "noArray" ).getObject() );
    }

    @Test
    public void array_setData_assigning_same_array_element_a_second_time_ovewrites_the_first_value()
    {
        Content content = newContent().build();
        content.setData( "array[0]", "First" );
        content.setData( "array[1]", "Second" );
        content.setData( "array[1]", "Second again" );

        assertEquals( "First", content.getData( "array[0]" ).getObject() );
        assertEquals( "Second again", content.getData( "array[1]" ).getObject() );
        assertNull( content.getData( "array[2]" ) );
    }

    @Test
    public void array_setData_setting_second_data_with_same_path_at_index_1_creates_array_of_size_2()
    {
        Content content = newContent().build();
        content.setData( "myArray", "First" );
        content.setData( "myArray[1]", "Second" );

        assertEquals( true, content.getData( "myArray" ).isArray() );
        assertEquals( 2, content.getData( "myArray" ).getArray().size() );
        assertEquals( "First", content.getData( "myArray[0]" ).getObject() );
        assertEquals( "Second", content.getData( "myArray[1]" ).getObject() );
        assertEquals( "myArray[0]", content.getData( "myArray" ).getPath().toString() );
    }

    @Test
    public void array_setData_array_within_set()
    {
        Content content = newContent().build();
        content.setData( "set.myArray[0]", "First" );
        content.setData( "set.myArray[1]", "Second" );

        assertEquals( "First", content.getData( "set.myArray[0]" ).getObject() );
        assertEquals( "Second", content.getData( "set.myArray[1]" ).getObject() );
        assertEquals( "set.myArray[0]", content.getData( "set.myArray" ).getPath().toString() );
        assertEquals( "First", content.getData( "set.myArray" ).asString( 0 ) );
        assertEquals( "Second", content.getData( "set.myArray" ).asString( 1 ) );
    }

    @Test
    public void array_setData_array_of_set_within_set()
    {
        Content content = newContent().build();
        content.setData( "company.address[0].street", "Kirkegata 1-3" );
        content.setData( "company.address[1].street", "Sonsteli" );

        assertEquals( "Kirkegata 1-3", content.getData( "company.address[0].street" ).asString() );
        assertEquals( "Sonsteli", content.getData( "company.address[1].street" ).asString() );
        assertEquals( "Sonsteli", content.getDataSet( "company" ).getDataSet( "address", 1 ).getData( "street" ).asString() );
        assertEquals( "Sonsteli", content.getDataSet( "company" ).getDataSet( "address[1]" ).getData( "street" ).asString() );
    }

    @Test
    public void add_array_of_set_within_set()
    {
        DataSet address1 = newDataSet().name( "address" ).build();
        address1.add( newData().name( "street" ).type( DataTypes.TEXT ).value( "Kirkegata 1-3" ).build() );

        DataSet address2 = newDataSet().name( "address" ).build();
        address2.add( newData().name( "street" ).type( DataTypes.TEXT ).value( "Sonsteli" ).build() );

        DataSet company = newDataSet().name( "company" ).build();
        company.add( address1 );
        company.add( address2 );
        RootDataSet rootDataSet = DataSet.newRootDataSet();
        rootDataSet.add( company );
        Content content = newContent().rootDataSet( rootDataSet ).build();

        assertEquals( "Kirkegata 1-3", content.getData( "company.address[0].street" ).getObject() );
        assertEquals( "Sonsteli", content.getData( "company.address[1].street" ).getObject() );
        assertEquals( "Sonsteli", content.getDataSet( "company" ).getDataSet( "address", 1 ).getData( "street" ).asString() );
        assertEquals( "Sonsteli", content.getDataSet( "company" ).getDataSet( "address[1]" ).getData( "street" ).asString() );
    }

    @Test
    public void array_set()
    {
        Content content = newContent().build();
        content.setData( "set[0].myText", "First" );
        content.setData( "set[1].myText", "Second" );

        assertEquals( "First", content.getData( "set.myText" ).getObject() );
        assertEquals( "First", content.getData( "set[0].myText" ).getObject() );
        assertEquals( "Second", content.getData( "set[1].myText" ).getObject() );
        assertEquals( true, content.getEntry( "set" ).isArray() );
        assertEquals( 0, content.getEntry( "set[0]" ).getArrayIndex() );
        assertEquals( "set[0]", content.getDataSet( "set[0]" ).getPath().toString() );
        assertEquals( "set[0].myText", content.getData( "set[0].myText" ).getPath().toString() );
        assertEquals( "set[1].myText", content.getData( "set[1].myText" ).getPath().toString() );
        assertEquals( "set[0]", content.getDataSet( "set[0]" ).getPath().toString() );
        assertEquals( "set[1]", content.getDataSet( "set[1]" ).getPath().toString() );
        assertEquals( "First", content.getDataSet( "set[0]" ).getData( "myText" ).asString() );
        assertEquals( "Second", content.getDataSet( "set[1]" ).getData( "myText" ).asString() );
    }

    @Test
    public void array_set2()
    {
        Content content = newContent().build();
        content.setData( "set[0].myText", "First" );
        content.setData( "set[0].myOther", "First other" );
        content.setData( "set[1].myText", "Second" );
        content.setData( "set[1].myOther", "Second other" );

        assertEquals( "First", content.getData( "set[0].myText" ).getObject() );
        assertEquals( "First other", content.getData( "set[0].myOther" ).getObject() );
        assertEquals( "Second", content.getData( "set[1].myText" ).getObject() );
        assertEquals( "Second other", content.getData( "set[1].myOther" ).getObject() );
        assertEquals( "set[0].myText", content.getData( "set[0].myText" ).getPath().toString() );
        assertEquals( "set[1].myText", content.getData( "set[1].myText" ).getPath().toString() );
        assertEquals( "set[0]", content.getDataSet( "set[0]" ).getPath().toString() );
        assertEquals( "set[1]", content.getDataSet( "set[1]" ).getPath().toString() );
        assertEquals( "First", content.getEntry( "set[0]" ).toDataSet().getData( "myText" ).asString() );
        assertEquals( "Second", content.getEntry( "set[1]" ).toDataSet().getData( "myText" ).asString() );
    }

    @Test
    public void tags()
    {
        contentType.form().addFormItem( newInput().name( "myTags" ).type( InputTypes.TAGS ).build() );

        // TODO: Are'nt tags best stored as an array? A global mixin multiple textline?
        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "myTags", "A line of text" );

        assertEquals( "A line of text", content.getData( "myTags" ).getObject() );
    }

    @Test
    public void tags_using_mixin()
    {
        Module module = newModule().name( "system" ).build();
        Input input = newInput().name( "tags" ).label( "Tags" ).type( InputTypes.TEXT_LINE ).multiple( true ).build();
        Mixin inputMixin = newMixin().module( module.getName() ).formItem( input ).build();
        MockMixinFetcher mixinFetcher = new MockMixinFetcher();
        mixinFetcher.add( inputMixin );

        contentType.form().addFormItem( newMixinReference().name( "myTags" ).mixin( "system:tags" ).type( Input.class ).build() );
        contentType.form().mixinReferencesToFormItems( mixinFetcher );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "myTags[0]", "Java" );
        content.setData( "myTags[1]", "XML" );
        content.setData( "myTags[2]", "JSON" );

        assertEquals( "Java", content.getData( "myTags" ).asString( 0 ) );
        assertEquals( "XML", content.getData( "myTags" ).asString( 1 ) );
        assertEquals( "JSON", content.getData( "myTags" ).asString( 2 ) );
    }

    @Test
    public void phone()
    {
        contentType.form().addFormItem( newInput().name( "myPhone" ).type( InputTypes.PHONE ).required( true ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "myPhone", "98327891" );

        assertEquals( "98327891", content.getData( "myPhone" ).getObject() );
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

        assertEquals( "Ola Nordmann", content.getData( "name" ).getObject() );
        assertEquals( "Blue", content.getData( "personalia.eyeColour" ).getObject() );
        assertEquals( "Blonde", content.getData( "personalia.hairColour" ).getObject() );
    }

    @Test
    public void multiple_mixin()
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

        assertEquals( "Norske", content.getData( "name" ).getObject() );
        assertEquals( "Ola Nordmann", content.getData( "personalia[0].name" ).getObject() );
        assertEquals( "Blue", content.getData( "personalia[0].eyeColour" ).getObject() );
        assertEquals( "Blonde", content.getData( "personalia[0].hairColour" ).getObject() );
        assertEquals( "Kari Trestakk", content.getData( "personalia[1].name" ).getObject() );
        assertEquals( "Green", content.getData( "personalia[1].eyeColour" ).getObject() );
        assertEquals( "Brown", content.getData( "personalia[1].hairColour" ).getObject() );
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

        assertEquals( "Thomas", content.getData( "firstName" ).getObject() );
        assertEquals( DataTypes.TEXT, content.getData( "firstName" ).getType() );
        assertEquals( DataTypes.HTML_PART, content.getData( "description" ).getType() );
        assertEquals( "Joachim", content.getData( "child[0].name" ).getObject() );
        assertEquals( "9", content.getData( "child[0].age" ).getObject() );
        assertEquals( "Blue", content.getData( "child[0].features.eyeColour" ).getObject() );
        assertEquals( "Blonde", content.getData( "child[0].features.hairColour" ).getObject() );
        assertEquals( "Madeleine", content.getData( "child[1].name" ).getObject() );
        assertEquals( "7", content.getData( "child[1].age" ).getObject() );
        assertEquals( "Brown", content.getData( "child[1].features.eyeColour" ).getObject() );
        assertEquals( "Black", content.getData( "child[1].features.hairColour" ).getObject() );
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

        DataSet child0 = content.getEntry( "child[0]" ).toDataSet();
        assertEquals( "Joachim", child0.getData( "name" ).getObject() );
        assertEquals( "9", child0.getData( "age" ).getObject() );
        assertEquals( "Blue", child0.getData( "features.eyeColour" ).getObject() );

        DataSet child1 = content.getEntry( "child[1]" ).toDataSet();
        assertEquals( "Madeleine", child1.getData( "name" ).getObject() );
        assertEquals( "7", child1.getData( "age" ).getObject() );
        assertEquals( "Brown", child1.getData( "features.eyeColour" ).getObject() );
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

        DataSet child0 = content.getEntry( "child[0]" ).toDataSet();
        assertEquals( "Joachim", child0.getData( "name" ).getObject() );
        assertEquals( "9", child0.getData( "age" ).getObject() );
        assertEquals( "Blue", child0.getData( "features.eyeColour" ).getObject() );

        DataSet child1 = content.getEntry( "child[1]" ).toDataSet();
        assertEquals( "Madeleine", child1.getData( "name" ).getObject() );
        assertEquals( "7", child1.getData( "age" ).getObject() );
        assertEquals( "Brown", child1.getData( "features.eyeColour" ).getObject() );
    }

    @Test
    public void given_unstructured_content_when_getting_values_then_they_are_returned()
    {
        // setup
        Content content = newContent().build();
        content.setData( "name", "Thomas" );
        content.setData( "personalia.eyeColour", "Blue", DataTypes.TEXT );
        content.setData( "personalia.hairColour", "Blonde" );

        assertEquals( DataTypes.TEXT, content.getData( "personalia.eyeColour" ).getType() );
        assertEquals( "Blue", content.getData( "personalia.eyeColour" ).getObject() );
        assertEquals( "personalia.eyeColour", content.getData( "personalia.eyeColour" ).getPath().toString() );
    }

    @Test
    public void mixins()
    {
        Module module = newModule().name( "myModule" ).build();

        Mixin postalCodeMixin =
            newMixin().module( module.getName() ).formItem( newInput().name( "postalCode" ).type( InputTypes.TEXT_LINE ).build() ).build();
        Mixin countryMixin = newMixin().module( module.getName() ).formItem(
            newInput().name( "country" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig(
                newSingleSelectorConfig().typeDropdown().addOption( "Norway", "NO" ).build() ).build() ).build();

        Mixin addressMixin = newMixin().module( module.getName() ).formItem(
            newFormItemSet().name( "address" ).add( newInput().name( "street" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newMixinReference( postalCodeMixin ).name( "postalCode" ).build() ).add(
                newInput().name( "postalPlace" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newMixinReference( countryMixin ).name( "country" ).build() ).build() ).build();

        contentType.form().addFormItem( newInput().type( InputTypes.TEXT_LINE ).name( "name" ).build() );
        contentType.form().addFormItem( newMixinReference( addressMixin ).name( "address" ).build() );

        MockMixinFetcher mixinFetcher = new MockMixinFetcher();
        mixinFetcher.add( postalCodeMixin );
        mixinFetcher.add( countryMixin );
        mixinFetcher.add( addressMixin );
        contentType.form().mixinReferencesToFormItems( mixinFetcher );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "name", "Ola Normann" );
        content.setData( "address.street", "Bakkebygrenda 1" );
        content.setData( "address.postalCode", "2676" );
        content.setData( "address.postalPlace", "Heidal" );
        content.setData( "address.country", "NO" );

        assertEquals( "Ola Normann", content.getData( "name" ).asString() );
        assertEquals( "Bakkebygrenda 1", content.getData( "address.street" ).asString() );
        assertEquals( "2676", content.getData( "address.postalCode" ).asString() );
        assertEquals( "Heidal", content.getData( "address.postalPlace" ).asString() );
        assertEquals( "NO", content.getData( "address.country" ).asString() );
    }

    @Test
    public void mixins_multiple()
    {
        Module module = newModule().name( "myModule" ).build();

        Mixin addressMixin = newMixin().module( module.getName() ).formItem(
            newFormItemSet().name( "address" ).multiple( true ).add( newInput().type( InputTypes.TEXT_LINE ).name( "label" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "street" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "postalCode" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "postalPlace" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "country" ).build() ).build() ).build();

        contentType.form().addFormItem( newMixinReference( addressMixin ).name( "address" ).build() );

        MockMixinFetcher mixinFetcher = new MockMixinFetcher();
        mixinFetcher.add( addressMixin );
        contentType.form().mixinReferencesToFormItems( mixinFetcher );

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

        assertEquals( "Home", content.getData( "address[0].label" ).asString() );
        assertEquals( "Bakkebygrenda 1", content.getData( "address[0].street" ).asString() );
        assertEquals( "2676", content.getData( "address[0].postalCode" ).asString() );
        assertEquals( "Heidal", content.getData( "address[0].postalPlace" ).asString() );
        assertEquals( "NO", content.getData( "address[0].country" ).asString() );

        assertEquals( "Cabin", content.getData( "address[1].label" ).asString() );
        assertEquals( "Heia", content.getData( "address[1].street" ).asString() );
        assertEquals( "2676", content.getData( "address[1].postalCode" ).asString() );
        assertEquals( "Gjende", content.getData( "address[1].postalPlace" ).asString() );
        assertEquals( "NO", content.getData( "address[1].country" ).asString() );
    }

    @Test
    public void trying_to_set_data_to_a_formItemSetMixin_when_mixin_is_missing()
    {
        contentType.form().addFormItem( newInput().type( InputTypes.TEXT_LINE ).name( "name" ).build() );
        contentType.form().addFormItem( newMixinReference().name( "address" ).typeInput().mixin( "myModule:myAddressMixin" ).build() );

        contentType.form().mixinReferencesToFormItems( new MockMixinFetcher() );

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
        assertEquals( "Ola Norman", content.getData( "name" ).asString() );
        assertEquals( "Blue", content.getData( "eyeColour" ).asString() );
        assertEquals( "Blonde", content.getData( "hairColour" ).asString() );
        assertEquals( "Skull on left arm", content.getData( "tattoo[0]" ).asString() );
        assertEquals( "Mothers name on right arm", content.getData( "tattoo[1]" ).asString() );
        assertEquals( "Chin", content.getData( "scar[0]" ).asString() );
    }

    @Test
    public void given_array_when_setting_data_of_another_type_to_array_then_exception_is_thrown()
    {
        // setup
        Content content = newContent().build();
        content.setData( "myData", "Value 1", DataTypes.TEXT );

        // exercise
        try
        {
            content.setData( "myData[1]", new DateMidnight( 2000, 1, 1 ), DataTypes.DATE );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Array [myData] expects Data of type [Text]. Data [myData] was of type: Date", e.getMessage() );
        }
    }

    @Test
    public void new_way()
    {
        RootDataSet rootDataSet = DataSet.newRootDataSet();
        rootDataSet.add( newData().type( DataTypes.TEXT ).name( "myData" ).value( "1" ).build() );
        rootDataSet.add( newText().name( "myData" ).value( "1" ).build() );
        rootDataSet.add( newXml().name( "myXml" ).value( "<root/>" ).build() );

        Content content = newContent().name( "myContent" ).rootDataSet( rootDataSet ).build();

        assertEquals( "1", content.getData( "myData" ).getValue().asString() );

        assertEquals( "1", content.getData( "myData" ).asString() );
    }

    @Test
    public void new_way2()
    {
        RootDataSet rootDataSet = DataSet.newRootDataSet();

        rootDataSet.add( new Text( "myData", "1" ) );
        rootDataSet.add( new Text( "myArray", "1" ) );
        rootDataSet.add( new Text( "myArray", "2" ) );
        //rootDataSet.add( new Xml( "myXml", "<root></root>" ) );

        Content content = newContent().name( "myContent" ).rootDataSet( rootDataSet ).build();

        assertEquals( "1", content.getData( "myArray" ).getObject() );
        assertEquals( "1", content.getRootDataSet().getData( "myArray", 0 ).getObject() );
        assertEquals( "2", content.getRootDataSet().getData( "myArray[1]" ).getObject() );
        assertEquals( true, content.getRootDataSet().getData( "myArray[1]" ).isArray() );
        assertEquals( true, content.getRootDataSet().getData( "myArray" ).isArray() );
        assertEquals( false, content.getRootDataSet().getData( "myData" ).isArray() );
        assertEquals( 0, content.getRootDataSet().getData( "myData" ).getArrayIndex() );
        assertEquals( 0, content.getRootDataSet().getData( "myArray" ).getArrayIndex() );
        assertEquals( 1, content.getRootDataSet().getData( "myArray[1]" ).getArrayIndex() );
        assertEquals( 2, content.getRootDataSet().entryCount( "myArray" ) );
        assertEquals( 1, content.getRootDataSet().entryCount( "myData" ) );

        Data myArray = content.getRootDataSet().getData( "myArray" );
    }
}
