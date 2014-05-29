package com.enonic.wem.api.content;


import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentType;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.data.DataSet.newDataSet;
import static com.enonic.wem.api.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static org.junit.Assert.*;

public class ContentTest
{
    private static final ContentPath MY_CONTENT_PATH = ContentPath.from( "/mycontent" );

    private ContentType contentType;

    @Before
    public void before()
    {
        contentType = newContentType().
            name( "mytype" ).
            build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_path_isRoot_then_IllegalArgumentException_is_thrown()
    {
        Content content = Content.newContent().path( ContentPath.ROOT ).build();
        assertEquals( false, content.isRoot() );
    }

    @Test
    public void isRoot_given_path_with_one_element_then_true_is_returned()
    {
        Content content = Content.newContent().path( "/myroot" ).build();
        assertEquals( true, content.isRoot() );
    }

    @Test
    public void isRoot_given_path_with_more_than_one_element_then_false_is_returned()
    {
        Content content = Content.newContent().path( "/myroot/mysub" ).build();
        assertEquals( false, content.isRoot() );
    }

    @Test
    public void array_getting_data_from_array_of_size_one()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( "array[0]", Value.newString( "First" ) );

        assertEquals( "First", dataSet.getProperty( "array" ).getObject() );
        assertEquals( "First", dataSet.getProperty( "array[0]" ).getObject() );
    }

    @Test
    public void array_getting_entries_from_array_of_size_two()
    {
        ContentData contentData = new ContentData();
        contentData.setProperty( "array[0]", Value.newString( "First" ) );
        contentData.setProperty( "array[1]", Value.newString( "Second" ) );

        Property array = contentData.getProperty( "array" );
        assertEquals( "First", array.getObject() );
        assertEquals( "First", contentData.getProperty( "array" ).getString( 0 ) );
        assertEquals( "First", contentData.getProperty( "array[0]" ).getString() );

        assertEquals( "Second", contentData.getProperty( "array" ).getString( 1 ) );
        assertEquals( "Second", contentData.getProperty( "array[1]" ).getString() );
    }

    @Test
    public void array()
    {
        Property first = Property.newString( "array", "First" );
        Property second = Property.newString( "array", "Second" );

        ContentData contentData = new ContentData();
        contentData.add( first );
        contentData.add( second );

        Property array = contentData.getProperty( "array" );
        assertEquals( "First", array.getObject() );
        assertEquals( "First", contentData.getProperty( "array" ).getString( 0 ) );
        assertEquals( "First", contentData.getProperty( "array[0]" ).getObject() );

        assertEquals( "Second", contentData.getProperty( "array" ).getString( 1 ) );
        assertEquals( "Second", contentData.getProperty( "array[1]" ).getString() );
    }

    @Test
    public void array_getting_entries_from_array_of_size_three()
    {
        ContentData contentData = new ContentData();
        contentData.setProperty( "array[0]", Value.newString( "First" ) );
        contentData.setProperty( "array[1]", Value.newString( "Second" ) );
        contentData.setProperty( "array[2]", Value.newString( "Third" ) );

        assertEquals( "First", contentData.getProperty( "array" ).getObject() );
        assertEquals( "First", contentData.getProperty( "array[0]" ).getObject() );

        assertEquals( "Second", contentData.getProperty( "array[1]" ).getObject() );

        assertEquals( "Third", contentData.getProperty( "array[2]" ).getObject() );
    }

    @Test
    public void array_overwriting_does_not_create_array()
    {
        ContentData contentData = new ContentData();
        contentData.setProperty( "noArray", Value.newString( "First" ) );
        contentData.setProperty( "noArray", Value.newString( "Second" ) );
        contentData.setProperty( "noArray", Value.newString( "Third" ) );

        assertEquals( "Third", contentData.getProperty( "noArray" ).getObject() );
    }

    @Test
    public void array_setData_assigning_same_array_element_a_second_time_ovewrites_the_first_value()
    {
        ContentData contentData = new ContentData();
        contentData.setProperty( "array[0]", Value.newString( "First" ) );
        contentData.setProperty( "array[1]", Value.newString( "Second" ) );
        contentData.setProperty( "array[1]", Value.newString( "Second again" ) );

        assertEquals( "First", contentData.getProperty( "array[0]" ).getObject() );
        assertEquals( "Second again", contentData.getProperty( "array[1]" ).getObject() );
        assertNull( contentData.getProperty( "array[2]" ) );
    }

    @Test
    public void array_setData_setting_second_data_with_same_path_at_index_1_creates_array_of_size_2()
    {
        ContentData contentData = new ContentData();
        contentData.setProperty( "myArray", Value.newString( "First" ) );
        contentData.setProperty( "myArray[1]", Value.newString( "Second" ) );

        assertEquals( true, contentData.getProperty( "myArray" ).isArray() );
        assertEquals( 2, contentData.getProperty( "myArray" ).getArray().size() );
        assertEquals( "First", contentData.getProperty( "myArray[0]" ).getObject() );
        assertEquals( "Second", contentData.getProperty( "myArray[1]" ).getObject() );
        assertEquals( "myArray[0]", contentData.getProperty( "myArray" ).getPath().toString() );
    }

    @Test
    public void array_setData_array_within_set()
    {
        ContentData contentData = new ContentData();
        contentData.setProperty( "set.myArray[0]", Value.newString( "First" ) );
        contentData.setProperty( "set.myArray[1]", Value.newString( "Second" ) );

        assertEquals( "First", contentData.getProperty( "set.myArray[0]" ).getObject() );
        assertEquals( "Second", contentData.getProperty( "set.myArray[1]" ).getObject() );
        assertEquals( "set.myArray[0]", contentData.getProperty( "set.myArray" ).getPath().toString() );
        assertEquals( "First", contentData.getProperty( "set.myArray" ).getString( 0 ) );
        assertEquals( "Second", contentData.getProperty( "set.myArray" ).getString( 1 ) );
    }

    @Test
    public void array_setData_array_of_set_within_set()
    {
        ContentData contentData = new ContentData();
        contentData.setProperty( "company.address[0].street", Value.newString( "Kirkegata 1-3" ) );
        contentData.setProperty( "company.address[1].street", Value.newString( "Sonsteli" ) );

        assertEquals( "Kirkegata 1-3", contentData.getProperty( "company.address[0].street" ).getString() );
        assertEquals( "Sonsteli", contentData.getProperty( "company.address[1].street" ).getString() );
        assertEquals( "Sonsteli", contentData.getDataSet( "company" ).getDataSet( "address", 1 ).getProperty( "street" ).getString() );
        assertEquals( "Sonsteli", contentData.getDataSet( "company" ).getDataSet( "address[1]" ).getProperty( "street" ).getString() );
    }

    @Test
    public void add_array_of_set_within_set()
    {
        DataSet address1 = newDataSet().name( "address" ).build();
        address1.add( Property.newString( "street", "Kirkegata 1-3" ) );

        DataSet address2 = newDataSet().name( "address" ).build();
        address2.add( Property.newString( "street", "Sonsteli" ) );

        DataSet company = newDataSet().name( "company" ).build();
        company.add( address1 );
        company.add( address2 );
        ContentData contentData = new ContentData();
        contentData.add( company );

        assertEquals( "Kirkegata 1-3", contentData.getProperty( "company.address[0].street" ).getObject() );
        assertEquals( "Sonsteli", contentData.getProperty( "company.address[1].street" ).getObject() );
        assertEquals( "Sonsteli", contentData.getDataSet( "company" ).getDataSet( "address", 1 ).getProperty( "street" ).getString() );
        assertEquals( "Sonsteli", contentData.getDataSet( "company" ).getDataSet( "address[1]" ).getProperty( "street" ).getString() );
    }

    @Test
    public void array_set()
    {
        ContentData contentData = new ContentData();
        contentData.setProperty( "set[0].myText", Value.newString( "First" ) );
        contentData.setProperty( "set[1].myText", Value.newString( "Second" ) );

        assertEquals( "First", contentData.getProperty( "set.myText" ).getObject() );
        assertEquals( "First", contentData.getProperty( "set[0].myText" ).getObject() );
        assertEquals( "Second", contentData.getProperty( "set[1].myText" ).getObject() );
        assertEquals( true, contentData.getDataSet( "set" ).isArray() );
        assertEquals( 0, contentData.getDataSet( "set[0]" ).getArrayIndex() );
        assertEquals( "set[0]", contentData.getDataSet( "set[0]" ).getPath().toString() );
        assertEquals( "set[0].myText", contentData.getProperty( "set[0].myText" ).getPath().toString() );
        assertEquals( "set[1].myText", contentData.getProperty( "set[1].myText" ).getPath().toString() );
        assertEquals( "set[0]", contentData.getDataSet( "set[0]" ).getPath().toString() );
        assertEquals( "set[1]", contentData.getDataSet( "set[1]" ).getPath().toString() );
        assertEquals( "First", contentData.getDataSet( "set[0]" ).getProperty( "myText" ).getString() );
        assertEquals( "Second", contentData.getDataSet( "set[1]" ).getProperty( "myText" ).getString() );
    }

    @Test
    public void array_set2()
    {
        ContentData contentData = new ContentData();
        contentData.setProperty( "set[0].myText", Value.newString( "First" ) );
        contentData.setProperty( "set[0].myOther", Value.newString( "First other" ) );
        contentData.setProperty( "set[1].myText", Value.newString( "Second" ) );
        contentData.setProperty( "set[1].myOther", Value.newString( "Second other" ) );

        assertEquals( "First", contentData.getProperty( "set[0].myText" ).getObject() );
        assertEquals( "First other", contentData.getProperty( "set[0].myOther" ).getObject() );
        assertEquals( "Second", contentData.getProperty( "set[1].myText" ).getObject() );
        assertEquals( "Second other", contentData.getProperty( "set[1].myOther" ).getObject() );
        assertEquals( "set[0].myText", contentData.getProperty( "set[0].myText" ).getPath().toString() );
        assertEquals( "set[1].myText", contentData.getProperty( "set[1].myText" ).getPath().toString() );
        assertEquals( "set[0]", contentData.getDataSet( "set[0]" ).getPath().toString() );
        assertEquals( "set[1]", contentData.getDataSet( "set[1]" ).getPath().toString() );
        assertEquals( "First", contentData.getData( "set[0]" ).toDataSet().getProperty( "myText" ).getString() );
        assertEquals( "Second", contentData.getData( "set[1]" ).toDataSet().getProperty( "myText" ).getString() );
    }

    @Test
    public void tags()
    {
        contentType.form().addFormItem( newInput().name( "myTags" ).inputType( InputTypes.TAGS ).build() );

        // TODO: Are'nt tags best stored as an array? A global mixin multiple textline?
        Content content = newContent().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getContentData().setProperty( "myTags", Value.newString( "A line of text" ) );

        assertEquals( "A line of text", content.getContentData().getProperty( "myTags" ).getObject() );
    }

    @Test
    public void phone()
    {
        contentType.form().addFormItem( newInput().name( "myPhone" ).inputType( InputTypes.PHONE ).required( true ).build() );

        Content content = newContent().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getContentData().setProperty( "myPhone", Value.newString( "98327891" ) );

        assertEquals( "98327891", content.getContentData().getProperty( "myPhone" ).getObject() );
    }

    @Test
    public void formItemSet()
    {
        contentType.form().addFormItem( newInput().name( "name" ).inputType( InputTypes.TEXT_LINE ).required( true ).build() );

        FormItemSet formItemSet = newFormItemSet().name( "personalia" ).build();
        contentType.form().addFormItem( formItemSet );
        formItemSet.add( newInput().name( "eyeColour" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "hairColour" ).inputType( InputTypes.TEXT_LINE ).build() );

        Content content = newContent().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        ContentData contentData = content.getContentData();
        contentData.setProperty( "name", Value.newString( "Ola Nordmann" ) );
        contentData.setProperty( "personalia.eyeColour", Value.newString( "Blue" ) );
        contentData.setProperty( "personalia.hairColour", Value.newString( "Blonde" ) );

        assertEquals( "Ola Nordmann", contentData.getProperty( "name" ).getObject() );
        assertEquals( "Blue", contentData.getProperty( "personalia.eyeColour" ).getObject() );
        assertEquals( "Blonde", contentData.getProperty( "personalia.hairColour" ).getObject() );
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

        Content content = newContent().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        ContentData contentData = content.getContentData();
        contentData.setProperty( "name", Value.newString( "Norske" ) );
        contentData.setProperty( "personalia[0].name", Value.newString( "Ola Nordmann" ) );
        contentData.setProperty( "personalia[0].eyeColour", Value.newString( "Blue" ) );
        contentData.setProperty( "personalia[0].hairColour", Value.newString( "Blonde" ) );
        contentData.setProperty( "personalia[1].name", Value.newString( "Kari Trestakk" ) );
        contentData.setProperty( "personalia[1].eyeColour", Value.newString( "Green" ) );
        contentData.setProperty( "personalia[1].hairColour", Value.newString( "Brown" ) );

        assertEquals( "Norske", contentData.getProperty( "name" ).getObject() );
        assertEquals( "Ola Nordmann", contentData.getProperty( "personalia[0].name" ).getObject() );
        assertEquals( "Blue", contentData.getProperty( "personalia[0].eyeColour" ).getObject() );
        assertEquals( "Blonde", contentData.getProperty( "personalia[0].hairColour" ).getObject() );
        assertEquals( "Kari Trestakk", contentData.getProperty( "personalia[1].name" ).getObject() );
        assertEquals( "Green", contentData.getProperty( "personalia[1].eyeColour" ).getObject() );
        assertEquals( "Brown", contentData.getProperty( "personalia[1].hairColour" ).getObject() );
    }

    @Test
    public void unstructured()
    {
        Content content = newContent().path( MY_CONTENT_PATH ).build();
        ContentData contentData = content.getContentData();
        contentData.setProperty( "firstName", Value.newString( "Thomas" ) );
        contentData.setProperty( "description", Value.newHtmlPart( "Grew up in Noetteveien" ) );
        contentData.setProperty( "child[0].name", Value.newString( "Joachim" ) );
        contentData.setProperty( "child[0].age", Value.newString( "9" ) );
        contentData.setProperty( "child[0].features.eyeColour", Value.newString( "Blue" ) );
        contentData.setProperty( "child[0].features.hairColour", Value.newString( "Blonde" ) );
        contentData.setProperty( "child[1].name", Value.newString( "Madeleine" ) );
        contentData.setProperty( "child[1].age", Value.newString( "7" ) );
        contentData.setProperty( "child[1].features.eyeColour", Value.newString( "Brown" ) );
        contentData.setProperty( "child[1].features.hairColour", Value.newString( "Black" ) );

        assertEquals( "Thomas", contentData.getProperty( "firstName" ).getObject() );
        assertEquals( ValueTypes.STRING, contentData.getProperty( "firstName" ).getValueType() );
        assertEquals( ValueTypes.HTML_PART, contentData.getProperty( "description" ).getValueType() );
        assertEquals( "Joachim", contentData.getProperty( "child[0].name" ).getObject() );
        assertEquals( "9", contentData.getProperty( "child[0].age" ).getObject() );
        assertEquals( "Blue", contentData.getProperty( "child[0].features.eyeColour" ).getObject() );
        assertEquals( "Blonde", contentData.getProperty( "child[0].features.hairColour" ).getObject() );
        assertEquals( "Madeleine", contentData.getProperty( "child[1].name" ).getObject() );
        assertEquals( "7", contentData.getProperty( "child[1].age" ).getObject() );
        assertEquals( "Brown", contentData.getProperty( "child[1].features.eyeColour" ).getObject() );
        assertEquals( "Black", contentData.getProperty( "child[1].features.hairColour" ).getObject() );
    }

    @Test
    public void unstructured_getEntries()
    {
        Content content = newContent().path( MY_CONTENT_PATH ).build();
        ContentData contentData = content.getContentData();
        contentData.setProperty( "child[0].name", Value.newString( "Joachim" ) );
        contentData.setProperty( "child[0].age", Value.newString( "9" ) );
        contentData.setProperty( "child[0].features.eyeColour", Value.newString( "Blue" ) );
        contentData.setProperty( "child[0].features.hairColour", Value.newString( "Blonde" ) );
        contentData.setProperty( "child[1].name", Value.newString( "Madeleine" ) );
        contentData.setProperty( "child[1].age", Value.newString( "7" ) );
        contentData.setProperty( "child[1].features.eyeColour", Value.newString( "Brown" ) );
        contentData.setProperty( "child[1].features.hairColour", Value.newString( "Black" ) );

        DataSet child0 = contentData.getData( "child[0]" ).toDataSet();
        assertEquals( "Joachim", child0.getProperty( "name" ).getObject() );
        assertEquals( "9", child0.getProperty( "age" ).getObject() );
        assertEquals( "Blue", child0.getProperty( "features.eyeColour" ).getObject() );

        DataSet child1 = contentData.getData( "child[1]" ).toDataSet();
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

        Content content = newContent().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        ContentData contentData = content.getContentData();
        contentData.setProperty( "child[0].name", Value.newString( "Joachim" ) );
        contentData.setProperty( "child[0].age", Value.newString( "9" ) );
        contentData.setProperty( "child[0].features.eyeColour", Value.newString( "Blue" ) );
        contentData.setProperty( "child[0].features.hairColour", Value.newString( "Blonde" ) );
        contentData.setProperty( "child[1].name", Value.newString( "Madeleine" ) );
        contentData.setProperty( "child[1].age", Value.newString( "7" ) );
        contentData.setProperty( "child[1].features.eyeColour", Value.newString( "Brown" ) );
        contentData.setProperty( "child[1].features.hairColour", Value.newString( "Black" ) );

        DataSet child0 = contentData.getData( "child[0]" ).toDataSet();
        assertEquals( "Joachim", child0.getProperty( "name" ).getObject() );
        assertEquals( "9", child0.getProperty( "age" ).getObject() );
        assertEquals( "Blue", child0.getProperty( "features.eyeColour" ).getObject() );

        DataSet child1 = contentData.getData( "child[1]" ).toDataSet();
        assertEquals( "Madeleine", child1.getProperty( "name" ).getObject() );
        assertEquals( "7", child1.getProperty( "age" ).getObject() );
        assertEquals( "Brown", child1.getProperty( "features.eyeColour" ).getObject() );
    }

    @Test
    public void given_unstructured_content_when_getting_values_then_they_are_returned()
    {
        // setup
        Content content = newContent().path( MY_CONTENT_PATH ).build();
        ContentData contentData = content.getContentData();
        contentData.setProperty( "name", Value.newString( "Thomas" ) );
        contentData.setProperty( "personalia.eyeColour", Value.newString( "Blue" ) );
        contentData.setProperty( "personalia.hairColour", Value.newString( "Blonde" ) );

        assertEquals( ValueTypes.STRING, contentData.getProperty( "personalia.eyeColour" ).getValueType() );
        assertEquals( "Blue", contentData.getProperty( "personalia.eyeColour" ).getObject() );
        assertEquals( "personalia.eyeColour", contentData.getProperty( "personalia.eyeColour" ).getPath().toString() );
    }

    @Test
    public void layout()
    {
        // setup
        contentType.form().addFormItem( newInput().name( "name" ).inputType( InputTypes.TEXT_LINE ).build() );
        FieldSet personalia = newFieldSet().label( "Personalia" ).name( "personalia" ).addFormItem(
            newInput().name( "eyeColour" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newInput().name( "hairColour" ).inputType( InputTypes.TEXT_LINE ).build() ).build();
        FieldSet tatoos = newFieldSet().label( "Characteristics" ).name( "characteristics" ).addFormItem(
            newInput().name( "tattoo" ).inputType( InputTypes.TEXT_LINE ).multiple( true ).build() ).addFormItem(
            newInput().name( "scar" ).inputType( InputTypes.TEXT_LINE ).multiple( true ).build() ).build();
        personalia.addFormItem( tatoos );
        contentType.form().addFormItem( personalia );

        Content content = newContent().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        // exercise
        ContentData contentData = content.getContentData();
        contentData.setProperty( "name", Value.newString( "Ola Norman" ) );
        contentData.setProperty( "eyeColour", Value.newString( "Blue" ) );
        contentData.setProperty( "hairColour", Value.newString( "Blonde" ) );
        contentData.setProperty( "tattoo[0]", Value.newString( "Skull on left arm" ) );
        contentData.setProperty( "tattoo[1]", Value.newString( "Mothers name on right arm" ) );
        contentData.setProperty( "scar[0]", Value.newString( "Chin" ) );

        // verify
        assertEquals( "Ola Norman", contentData.getProperty( "name" ).getString() );
        assertEquals( "Blue", contentData.getProperty( "eyeColour" ).getString() );
        assertEquals( "Blonde", contentData.getProperty( "hairColour" ).getString() );
        assertEquals( "Skull on left arm", contentData.getProperty( "tattoo[0]" ).getString() );
        assertEquals( "Mothers name on right arm", contentData.getProperty( "tattoo[1]" ).getString() );
        assertEquals( "Chin", contentData.getProperty( "scar[0]" ).getString() );
    }

    @Test
    public void given_array_when_setting_data_of_another_type_to_array_then_exception_is_thrown()
    {
        // setup
        Content content = newContent().path( MY_CONTENT_PATH ).build();
        content.getContentData().setProperty( "myData", Value.newString( "Value 1" ) );

        // exercise
        try
        {
            content.getContentData().setProperty( "myData[1]", Value.newLocalDate( LocalDate.of( 2000, 1, 1 ) ) );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Array [myData] expects Property of type [String]. Property [myData] was of type: LocalDate", e.getMessage() );
        }
    }
}
