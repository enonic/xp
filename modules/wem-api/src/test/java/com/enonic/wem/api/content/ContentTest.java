package com.enonic.wem.api.content;


import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.data.type.ValueTypes;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.form.FieldSet;
import com.enonic.wem.api.content.schema.content.form.FormItemSet;
import com.enonic.wem.api.content.schema.content.form.Input;
import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.data.DataSet.newDataSet;
import static com.enonic.wem.api.content.data.DataSet.newRootDataSet;
import static com.enonic.wem.api.content.data.Property.Text.newText;
import static com.enonic.wem.api.content.data.Property.Xml.newXml;
import static com.enonic.wem.api.content.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.content.schema.content.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.schema.content.form.Input.newInput;
import static org.junit.Assert.*;

public class ContentTest
{

    private ContentType contentType;

    @Before
    public void before()
    {
        contentType = newContentType().
            module( Module.SYSTEM.getName() ).
            name( "mytype" ).
            build();
    }

    @Test
    public void isEmbedded()
    {
        Content content = Content.newContent().path( ContentPath.from( "mySite:myParent/__embedded/myEmbedded" ) ).build();
        assertEquals( true, content.isEmbedded() );
    }

    @Test
    public void array_getting_entry_from_array_of_size_one()
    {
        DataSet dataSet = new RootDataSet();
        dataSet.setProperty( "array[0]", new Value.Text( "First" ) );

        assertEquals( "First", dataSet.getProperty( "array" ).getObject() );
        assertEquals( "First", dataSet.getProperty( "array[0]" ).getObject() );
    }

    @Test
    public void array_getting_entries_from_array_of_size_two()
    {
        RootDataSet rootDataSet = newRootDataSet();
        rootDataSet.setProperty( "array[0]", new Value.Text( "First" ) );
        rootDataSet.setProperty( "array[1]", new Value.Text( "Second" ) );

        Property array = rootDataSet.getProperty( "array" );
        assertEquals( "First", array.getObject() );
        assertEquals( "First", rootDataSet.getProperty( "array" ).getString( 0 ) );
        assertEquals( "First", rootDataSet.getProperty( "array[0]" ).getString() );

        assertEquals( "Second", rootDataSet.getProperty( "array" ).getString( 1 ) );
        assertEquals( "Second", rootDataSet.getProperty( "array[1]" ).getString() );
    }

    @Test
    public void array()
    {
        Property first = Property.newProperty().name( "array" ).type( ValueTypes.TEXT ).value( "First" ).build();
        Property second = Property.newProperty().name( "array" ).type( ValueTypes.TEXT ).value( "Second" ).build();

        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.add( first );
        rootDataSet.add( second );

        Property array = rootDataSet.getProperty( "array" );
        assertEquals( "First", array.getObject() );
        assertEquals( "First", rootDataSet.getProperty( "array" ).getString( 0 ) );
        assertEquals( "First", rootDataSet.getProperty( "array[0]" ).getObject() );

        assertEquals( "Second", rootDataSet.getProperty( "array" ).getString( 1 ) );
        assertEquals( "Second", rootDataSet.getProperty( "array[1]" ).getString() );
    }

    @Test
    public void array_getting_entries_from_array_of_size_three()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "array[0]", new Value.Text( "First" ) );
        rootDataSet.setProperty( "array[1]", new Value.Text( "Second" ) );
        rootDataSet.setProperty( "array[2]", new Value.Text( "Third" ) );

        assertEquals( "First", rootDataSet.getProperty( "array" ).getObject() );
        assertEquals( "First", rootDataSet.getProperty( "array[0]" ).getObject() );

        assertEquals( "Second", rootDataSet.getProperty( "array[1]" ).getObject() );

        assertEquals( "Third", rootDataSet.getProperty( "array[2]" ).getObject() );
    }

    @Test
    public void array_overwriting_does_not_create_array()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "noArray", new Value.Text( "First" ) );
        rootDataSet.setProperty( "noArray", new Value.Text( "Second" ) );
        rootDataSet.setProperty( "noArray", new Value.Text( "Third" ) );

        assertEquals( "Third", rootDataSet.getProperty( "noArray" ).getObject() );
    }

    @Test
    public void array_setData_assigning_same_array_element_a_second_time_ovewrites_the_first_value()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "array[0]", new Value.Text( "First" ) );
        rootDataSet.setProperty( "array[1]", new Value.Text( "Second" ) );
        rootDataSet.setProperty( "array[1]", new Value.Text( "Second again" ) );

        assertEquals( "First", rootDataSet.getProperty( "array[0]" ).getObject() );
        assertEquals( "Second again", rootDataSet.getProperty( "array[1]" ).getObject() );
        assertNull( rootDataSet.getProperty( "array[2]" ) );
    }

    @Test
    public void array_setData_setting_second_data_with_same_path_at_index_1_creates_array_of_size_2()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "myArray", new Value.Text( "First" ) );
        rootDataSet.setProperty( "myArray[1]", new Value.Text( "Second" ) );

        assertEquals( true, rootDataSet.getProperty( "myArray" ).isArray() );
        assertEquals( 2, rootDataSet.getProperty( "myArray" ).getArray().size() );
        assertEquals( "First", rootDataSet.getProperty( "myArray[0]" ).getObject() );
        assertEquals( "Second", rootDataSet.getProperty( "myArray[1]" ).getObject() );
        assertEquals( "myArray[0]", rootDataSet.getProperty( "myArray" ).getPath().toString() );
    }

    @Test
    public void array_setData_array_within_set()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "set.myArray[0]", new Value.Text( "First" ) );
        rootDataSet.setProperty( "set.myArray[1]", new Value.Text( "Second" ) );

        assertEquals( "First", rootDataSet.getProperty( "set.myArray[0]" ).getObject() );
        assertEquals( "Second", rootDataSet.getProperty( "set.myArray[1]" ).getObject() );
        assertEquals( "set.myArray[0]", rootDataSet.getProperty( "set.myArray" ).getPath().toString() );
        assertEquals( "First", rootDataSet.getProperty( "set.myArray" ).getString( 0 ) );
        assertEquals( "Second", rootDataSet.getProperty( "set.myArray" ).getString( 1 ) );
    }

    @Test
    public void array_setData_array_of_set_within_set()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "company.address[0].street", new Value.Text( "Kirkegata 1-3" ) );
        rootDataSet.setProperty( "company.address[1].street", new Value.Text( "Sonsteli" ) );

        assertEquals( "Kirkegata 1-3", rootDataSet.getProperty( "company.address[0].street" ).getString() );
        assertEquals( "Sonsteli", rootDataSet.getProperty( "company.address[1].street" ).getString() );
        assertEquals( "Sonsteli", rootDataSet.getDataSet( "company" ).getDataSet( "address", 1 ).getProperty( "street" ).getString() );
        assertEquals( "Sonsteli", rootDataSet.getDataSet( "company" ).getDataSet( "address[1]" ).getProperty( "street" ).getString() );
    }

    @Test
    public void add_array_of_set_within_set()
    {
        DataSet address1 = newDataSet().name( "address" ).build();
        address1.add( Property.newProperty().name( "street" ).type( ValueTypes.TEXT ).value( "Kirkegata 1-3" ).build() );

        DataSet address2 = newDataSet().name( "address" ).build();
        address2.add( Property.newProperty().name( "street" ).type( ValueTypes.TEXT ).value( "Sonsteli" ).build() );

        DataSet company = newDataSet().name( "company" ).build();
        company.add( address1 );
        company.add( address2 );
        RootDataSet rootDataSet = DataSet.newRootDataSet();
        rootDataSet.add( company );

        assertEquals( "Kirkegata 1-3", rootDataSet.getProperty( "company.address[0].street" ).getObject() );
        assertEquals( "Sonsteli", rootDataSet.getProperty( "company.address[1].street" ).getObject() );
        assertEquals( "Sonsteli", rootDataSet.getDataSet( "company" ).getDataSet( "address", 1 ).getProperty( "street" ).getString() );
        assertEquals( "Sonsteli", rootDataSet.getDataSet( "company" ).getDataSet( "address[1]" ).getProperty( "street" ).getString() );
    }

    @Test
    public void array_set()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "set[0].myText", new Value.Text( "First" ) );
        rootDataSet.setProperty( "set[1].myText", new Value.Text( "Second" ) );

        assertEquals( "First", rootDataSet.getProperty( "set.myText" ).getObject() );
        assertEquals( "First", rootDataSet.getProperty( "set[0].myText" ).getObject() );
        assertEquals( "Second", rootDataSet.getProperty( "set[1].myText" ).getObject() );
        assertEquals( true, rootDataSet.getDataSet( "set" ).isArray() );
        assertEquals( 0, rootDataSet.getDataSet( "set[0]" ).getArrayIndex() );
        assertEquals( "set[0]", rootDataSet.getDataSet( "set[0]" ).getPath().toString() );
        assertEquals( "set[0].myText", rootDataSet.getProperty( "set[0].myText" ).getPath().toString() );
        assertEquals( "set[1].myText", rootDataSet.getProperty( "set[1].myText" ).getPath().toString() );
        assertEquals( "set[0]", rootDataSet.getDataSet( "set[0]" ).getPath().toString() );
        assertEquals( "set[1]", rootDataSet.getDataSet( "set[1]" ).getPath().toString() );
        assertEquals( "First", rootDataSet.getDataSet( "set[0]" ).getProperty( "myText" ).getString() );
        assertEquals( "Second", rootDataSet.getDataSet( "set[1]" ).getProperty( "myText" ).getString() );
    }

    @Test
    public void array_set2()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "set[0].myText", new Value.Text( "First" ) );
        rootDataSet.setProperty( "set[0].myOther", new Value.Text( "First other" ) );
        rootDataSet.setProperty( "set[1].myText", new Value.Text( "Second" ) );
        rootDataSet.setProperty( "set[1].myOther", new Value.Text( "Second other" ) );

        assertEquals( "First", rootDataSet.getProperty( "set[0].myText" ).getObject() );
        assertEquals( "First other", rootDataSet.getProperty( "set[0].myOther" ).getObject() );
        assertEquals( "Second", rootDataSet.getProperty( "set[1].myText" ).getObject() );
        assertEquals( "Second other", rootDataSet.getProperty( "set[1].myOther" ).getObject() );
        assertEquals( "set[0].myText", rootDataSet.getProperty( "set[0].myText" ).getPath().toString() );
        assertEquals( "set[1].myText", rootDataSet.getProperty( "set[1].myText" ).getPath().toString() );
        assertEquals( "set[0]", rootDataSet.getDataSet( "set[0]" ).getPath().toString() );
        assertEquals( "set[1]", rootDataSet.getDataSet( "set[1]" ).getPath().toString() );
        assertEquals( "First", rootDataSet.getEntry( "set[0]" ).toDataSet().getProperty( "myText" ).getString() );
        assertEquals( "Second", rootDataSet.getEntry( "set[1]" ).toDataSet().getProperty( "myText" ).getString() );
    }

    @Test
    public void tags()
    {
        contentType.form().addFormItem( newInput().name( "myTags" ).inputType( InputTypes.TAGS ).build() );

        // TODO: Are'nt tags best stored as an array? A global mixin multiple textline?
        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getRootDataSet().setProperty( "myTags", new Value.Text( "A line of text" ) );

        assertEquals( "A line of text", content.getRootDataSet().getProperty( "myTags" ).getObject() );
    }

    @Test
    public void phone()
    {
        contentType.form().addFormItem( newInput().name( "myPhone" ).inputType( InputTypes.PHONE ).required( true ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getRootDataSet().setProperty( "myPhone", new Value.Text( "98327891" ) );

        assertEquals( "98327891", content.getRootDataSet().getProperty( "myPhone" ).getObject() );
    }

    @Test
    public void formItemSet()
    {
        contentType.form().addFormItem( newInput().name( "name" ).inputType( InputTypes.TEXT_LINE ).required( true ).build() );

        FormItemSet formItemSet = newFormItemSet().name( "personalia" ).build();
        contentType.form().addFormItem( formItemSet );
        formItemSet.add( newInput().name( "eyeColour" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "hairColour" ).inputType( InputTypes.TEXT_LINE ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        RootDataSet rootDataSet = content.getRootDataSet();
        rootDataSet.setProperty( "name", new Value.Text( "Ola Nordmann" ) );
        rootDataSet.setProperty( "personalia.eyeColour", new Value.Text( "Blue" ) );
        rootDataSet.setProperty( "personalia.hairColour", new Value.Text( "Blonde" ) );

        assertEquals( "Ola Nordmann", rootDataSet.getProperty( "name" ).getObject() );
        assertEquals( "Blue", rootDataSet.getProperty( "personalia.eyeColour" ).getObject() );
        assertEquals( "Blonde", rootDataSet.getProperty( "personalia.hairColour" ).getObject() );
    }

    @Test
    public void multiple_mixin()
    {
        Input nameInput = newInput().name( "name" ).inputType( InputTypes.TEXT_LINE ).required( true ).build();
        contentType.form().addFormItem( nameInput );

        FormItemSet formItemSet = newFormItemSet().name( "personalia" ).multiple( true ).build();
        contentType.form().addFormItem( formItemSet );
        formItemSet.add( newInput().name( "name" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "eyeColour" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "hairColour" ).inputType( InputTypes.TEXT_LINE ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        RootDataSet rootDataSet = content.getRootDataSet();
        rootDataSet.setProperty( "name", new Value.Text( "Norske" ) );
        rootDataSet.setProperty( "personalia[0].name", new Value.Text( "Ola Nordmann" ) );
        rootDataSet.setProperty( "personalia[0].eyeColour", new Value.Text( "Blue" ) );
        rootDataSet.setProperty( "personalia[0].hairColour", new Value.Text( "Blonde" ) );
        rootDataSet.setProperty( "personalia[1].name", new Value.Text( "Kari Trestakk" ) );
        rootDataSet.setProperty( "personalia[1].eyeColour", new Value.Text( "Green" ) );
        rootDataSet.setProperty( "personalia[1].hairColour", new Value.Text( "Brown" ) );

        assertEquals( "Norske", rootDataSet.getProperty( "name" ).getObject() );
        assertEquals( "Ola Nordmann", rootDataSet.getProperty( "personalia[0].name" ).getObject() );
        assertEquals( "Blue", rootDataSet.getProperty( "personalia[0].eyeColour" ).getObject() );
        assertEquals( "Blonde", rootDataSet.getProperty( "personalia[0].hairColour" ).getObject() );
        assertEquals( "Kari Trestakk", rootDataSet.getProperty( "personalia[1].name" ).getObject() );
        assertEquals( "Green", rootDataSet.getProperty( "personalia[1].eyeColour" ).getObject() );
        assertEquals( "Brown", rootDataSet.getProperty( "personalia[1].hairColour" ).getObject() );
    }

    @Test
    public void unstructured()
    {
        Content content = newContent().build();
        RootDataSet rootDataSet = content.getRootDataSet();
        rootDataSet.setProperty( "firstName", new Value.Text( "Thomas" ) );
        rootDataSet.setProperty( "description", new Value.HtmlPart( "Grew up in Noetteveien" ) );
        rootDataSet.setProperty( "child[0].name", new Value.Text( "Joachim" ) );
        rootDataSet.setProperty( "child[0].age", new Value.Text( "9" ) );
        rootDataSet.setProperty( "child[0].features.eyeColour", new Value.Text( "Blue" ) );
        rootDataSet.setProperty( "child[0].features.hairColour", new Value.Text( "Blonde" ) );
        rootDataSet.setProperty( "child[1].name", new Value.Text( "Madeleine" ) );
        rootDataSet.setProperty( "child[1].age", new Value.Text( "7" ) );
        rootDataSet.setProperty( "child[1].features.eyeColour", new Value.Text( "Brown" ) );
        rootDataSet.setProperty( "child[1].features.hairColour", new Value.Text( "Black" ) );

        assertEquals( "Thomas", rootDataSet.getProperty( "firstName" ).getObject() );
        assertEquals( ValueTypes.TEXT, rootDataSet.getProperty( "firstName" ).getType() );
        assertEquals( ValueTypes.HTML_PART, rootDataSet.getProperty( "description" ).getType() );
        assertEquals( "Joachim", rootDataSet.getProperty( "child[0].name" ).getObject() );
        assertEquals( "9", rootDataSet.getProperty( "child[0].age" ).getObject() );
        assertEquals( "Blue", rootDataSet.getProperty( "child[0].features.eyeColour" ).getObject() );
        assertEquals( "Blonde", rootDataSet.getProperty( "child[0].features.hairColour" ).getObject() );
        assertEquals( "Madeleine", rootDataSet.getProperty( "child[1].name" ).getObject() );
        assertEquals( "7", rootDataSet.getProperty( "child[1].age" ).getObject() );
        assertEquals( "Brown", rootDataSet.getProperty( "child[1].features.eyeColour" ).getObject() );
        assertEquals( "Black", rootDataSet.getProperty( "child[1].features.hairColour" ).getObject() );
    }

    @Test
    public void unstructured_getEntries()
    {
        Content content = newContent().build();
        RootDataSet rootDataSet = content.getRootDataSet();
        rootDataSet.setProperty( "child[0].name", new Value.Text( "Joachim" ) );
        rootDataSet.setProperty( "child[0].age", new Value.Text( "9" ) );
        rootDataSet.setProperty( "child[0].features.eyeColour", new Value.Text( "Blue" ) );
        rootDataSet.setProperty( "child[0].features.hairColour", new Value.Text( "Blonde" ) );
        rootDataSet.setProperty( "child[1].name", new Value.Text( "Madeleine" ) );
        rootDataSet.setProperty( "child[1].age", new Value.Text( "7" ) );
        rootDataSet.setProperty( "child[1].features.eyeColour", new Value.Text( "Brown" ) );
        rootDataSet.setProperty( "child[1].features.hairColour", new Value.Text( "Black" ) );

        DataSet child0 = rootDataSet.getEntry( "child[0]" ).toDataSet();
        assertEquals( "Joachim", child0.getProperty( "name" ).getObject() );
        assertEquals( "9", child0.getProperty( "age" ).getObject() );
        assertEquals( "Blue", child0.getProperty( "features.eyeColour" ).getObject() );

        DataSet child1 = rootDataSet.getEntry( "child[1]" ).toDataSet();
        assertEquals( "Madeleine", child1.getProperty( "name" ).getObject() );
        assertEquals( "7", child1.getProperty( "age" ).getObject() );
        assertEquals( "Brown", child1.getProperty( "features.eyeColour" ).getObject() );
    }

    @Test
    public void structured_getEntries()
    {
        FormItemSet child = newFormItemSet().name( "child" ).multiple( true ).build();
        child.add( newInput().name( "name" ).inputType( InputTypes.TEXT_LINE ).build() );
        child.add( newInput().name( "age" ).inputType( InputTypes.TEXT_LINE ).build() );
        FormItemSet features = newFormItemSet().name( "features" ).multiple( false ).build();
        features.add( newInput().name( "eyeColour" ).inputType( InputTypes.TEXT_LINE ).build() );
        features.add( newInput().name( "hairColour" ).inputType( InputTypes.TEXT_LINE ).build() );
        child.add( features );
        contentType.form().addFormItem( child );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        RootDataSet rootDataSet = content.getRootDataSet();
        rootDataSet.setProperty( "child[0].name", new Value.Text( "Joachim" ) );
        rootDataSet.setProperty( "child[0].age", new Value.Text( "9" ) );
        rootDataSet.setProperty( "child[0].features.eyeColour", new Value.Text( "Blue" ) );
        rootDataSet.setProperty( "child[0].features.hairColour", new Value.Text( "Blonde" ) );
        rootDataSet.setProperty( "child[1].name", new Value.Text( "Madeleine" ) );
        rootDataSet.setProperty( "child[1].age", new Value.Text( "7" ) );
        rootDataSet.setProperty( "child[1].features.eyeColour", new Value.Text( "Brown" ) );
        rootDataSet.setProperty( "child[1].features.hairColour", new Value.Text( "Black" ) );

        DataSet child0 = rootDataSet.getEntry( "child[0]" ).toDataSet();
        assertEquals( "Joachim", child0.getProperty( "name" ).getObject() );
        assertEquals( "9", child0.getProperty( "age" ).getObject() );
        assertEquals( "Blue", child0.getProperty( "features.eyeColour" ).getObject() );

        DataSet child1 = rootDataSet.getEntry( "child[1]" ).toDataSet();
        assertEquals( "Madeleine", child1.getProperty( "name" ).getObject() );
        assertEquals( "7", child1.getProperty( "age" ).getObject() );
        assertEquals( "Brown", child1.getProperty( "features.eyeColour" ).getObject() );
    }

    @Test
    public void given_unstructured_content_when_getting_values_then_they_are_returned()
    {
        // setup
        Content content = newContent().build();
        RootDataSet rootDataSet = content.getRootDataSet();
        rootDataSet.setProperty( "name", new Value.Text( "Thomas" ) );
        rootDataSet.setProperty( "personalia.eyeColour", new Value.Text( "Blue" ) );
        rootDataSet.setProperty( "personalia.hairColour", new Value.Text( "Blonde" ) );

        assertEquals( ValueTypes.TEXT, rootDataSet.getProperty( "personalia.eyeColour" ).getType() );
        assertEquals( "Blue", rootDataSet.getProperty( "personalia.eyeColour" ).getObject() );
        assertEquals( "personalia.eyeColour", rootDataSet.getProperty( "personalia.eyeColour" ).getPath().toString() );
    }

    @Test
    public void layout()
    {
        // setup
        contentType.form().addFormItem( newInput().name( "name" ).inputType( InputTypes.TEXT_LINE ).build() );
        FieldSet personalia = newFieldSet().label( "Personalia" ).name( "personalia" ).add(
            newInput().name( "eyeColour" ).inputType( InputTypes.TEXT_LINE ).build() ).add(
            newInput().name( "hairColour" ).inputType( InputTypes.TEXT_LINE ).build() ).build();
        FieldSet tatoos = newFieldSet().label( "Characteristics" ).name( "characteristics" ).add(
            newInput().name( "tattoo" ).inputType( InputTypes.TEXT_LINE ).multiple( true ).build() ).add(
            newInput().name( "scar" ).inputType( InputTypes.TEXT_LINE ).multiple( true ).build() ).build();
        personalia.addFormItem( tatoos );
        contentType.form().addFormItem( personalia );

        Content content = newContent().type( contentType.getQualifiedName() ).build();

        // exercise
        RootDataSet rootDataSet = content.getRootDataSet();
        rootDataSet.setProperty( "name", new Value.Text( "Ola Norman" ) );
        rootDataSet.setProperty( "eyeColour", new Value.Text( "Blue" ) );
        rootDataSet.setProperty( "hairColour", new Value.Text( "Blonde" ) );
        rootDataSet.setProperty( "tattoo[0]", new Value.Text( "Skull on left arm" ) );
        rootDataSet.setProperty( "tattoo[1]", new Value.Text( "Mothers name on right arm" ) );
        rootDataSet.setProperty( "scar[0]", new Value.Text( "Chin" ) );

        // verify
        assertEquals( "Ola Norman", rootDataSet.getProperty( "name" ).getString() );
        assertEquals( "Blue", rootDataSet.getProperty( "eyeColour" ).getString() );
        assertEquals( "Blonde", rootDataSet.getProperty( "hairColour" ).getString() );
        assertEquals( "Skull on left arm", rootDataSet.getProperty( "tattoo[0]" ).getString() );
        assertEquals( "Mothers name on right arm", rootDataSet.getProperty( "tattoo[1]" ).getString() );
        assertEquals( "Chin", rootDataSet.getProperty( "scar[0]" ).getString() );
    }

    @Test
    public void given_array_when_setting_data_of_another_type_to_array_then_exception_is_thrown()
    {
        // setup
        Content content = newContent().build();
        content.getRootDataSet().setProperty( "myData", new Value.Text( "Value 1" ) );

        // exercise
        try
        {
            content.getRootDataSet().setProperty( "myData[1]", new Value.Date( new DateMidnight( 2000, 1, 1 ) ) );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Array [myData] expects Property of type [Text]. Property [myData] was of type: DateMidnight", e.getMessage() );
        }
    }

    @Test
    public void new_way()
    {
        RootDataSet rootDataSet = DataSet.newRootDataSet();
        rootDataSet.add( Property.newProperty().type( ValueTypes.TEXT ).name( "myData" ).value( "1" ).build() );
        rootDataSet.add( newText().name( "myData" ).value( "1" ).build() );
        rootDataSet.add( newXml().name( "myXml" ).value( "<root/>" ).build() );

        assertEquals( "1", rootDataSet.getProperty( "myData" ).getValue().asString() );
        assertEquals( "1", rootDataSet.getProperty( "myData" ).getString() );
    }

    @Test
    public void new_way2()
    {
        RootDataSet rootDataSet = DataSet.newRootDataSet();

        rootDataSet.add( new Property.Text( "myData", "1" ) );
        rootDataSet.add( new Property.Text( "myArray", "1" ) );
        rootDataSet.add( new Property.Text( "myArray", "2" ) );
        //rootDataSet.add( new Xml( "myXml", "<root></root>" ) );

        Content content = newContent().name( "myContent" ).rootDataSet( rootDataSet ).build();

        assertEquals( "1", rootDataSet.getProperty( "myArray" ).getObject() );
        assertEquals( "1", rootDataSet.getProperty( "myArray", 0 ).getObject() );
        assertEquals( "2", rootDataSet.getProperty( "myArray[1]" ).getObject() );
        assertEquals( true, rootDataSet.getProperty( "myArray[1]" ).isArray() );
        assertEquals( true, rootDataSet.getProperty( "myArray" ).isArray() );
        assertEquals( false, rootDataSet.getProperty( "myData" ).isArray() );
        assertEquals( 0, rootDataSet.getProperty( "myData" ).getArrayIndex() );
        assertEquals( 0, rootDataSet.getProperty( "myArray" ).getArrayIndex() );
        assertEquals( 1, rootDataSet.getProperty( "myArray[1]" ).getArrayIndex() );
        assertEquals( 2, rootDataSet.entryCount( "myArray" ) );
        assertEquals( 1, rootDataSet.entryCount( "myData" ) );

        Property myArray = content.getRootDataSet().getProperty( "myArray" );
    }
}
