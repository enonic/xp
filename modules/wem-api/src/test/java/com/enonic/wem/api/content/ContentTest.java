package com.enonic.wem.api.content;


import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.form.FieldSet;
import com.enonic.wem.api.content.schema.content.form.FormItemSet;
import com.enonic.wem.api.content.schema.content.form.Input;
import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.data.Data.Text.newText;
import static com.enonic.wem.api.content.data.Data.Xml.newXml;
import static com.enonic.wem.api.content.data.Data.newData;
import static com.enonic.wem.api.content.data.DataSet.newDataSet;
import static com.enonic.wem.api.content.data.DataSet.newRootDataSet;
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
            name( "MyType" ).
            build();
    }

    @Test
    public void isEmbedded()
    {
        Content content = Content.newContent().path( ContentPath.from( "mySite:myParent/_embedded/myEmbedded" ) ).build();
        assertEquals( true, content.isEmbedded() );
    }

    @Test
    public void array_getting_entry_from_array_of_size_one()
    {
        DataSet dataSet = new RootDataSet();
        dataSet.setData( "array[0]", new Value.Text( "First" ) );

        assertEquals( "First", dataSet.getData( "array" ).getObject() );
        assertEquals( "First", dataSet.getData( "array[0]" ).getObject() );
    }

    @Test
    public void array_getting_entries_from_array_of_size_two()
    {
        RootDataSet rootDataSet = newRootDataSet();
        rootDataSet.setData( "array[0]", new Value.Text( "First" ) );
        rootDataSet.setData( "array[1]", new Value.Text( "Second" ) );

        Data array = rootDataSet.getData( "array" );
        assertEquals( "First", array.getObject() );
        assertEquals( "First", rootDataSet.getData( "array" ).getString( 0 ) );
        assertEquals( "First", rootDataSet.getData( "array[0]" ).getString() );

        assertEquals( "Second", rootDataSet.getData( "array" ).getString( 1 ) );
        assertEquals( "Second", rootDataSet.getData( "array[1]" ).getString() );
    }

    @Test
    public void array()
    {
        Data first = newData().name( "array" ).type( DataTypes.TEXT ).value( "First" ).build();
        Data second = newData().name( "array" ).type( DataTypes.TEXT ).value( "Second" ).build();

        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.add( first );
        rootDataSet.add( second );

        Data array = rootDataSet.getData( "array" );
        assertEquals( "First", array.getObject() );
        assertEquals( "First", rootDataSet.getData( "array" ).getString( 0 ) );
        assertEquals( "First", rootDataSet.getData( "array[0]" ).getObject() );

        assertEquals( "Second", rootDataSet.getData( "array" ).getString( 1 ) );
        assertEquals( "Second", rootDataSet.getData( "array[1]" ).getString() );
    }

    @Test
    public void array_getting_entries_from_array_of_size_three()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setData( "array[0]", new Value.Text( "First" ) );
        rootDataSet.setData( "array[1]", new Value.Text( "Second" ) );
        rootDataSet.setData( "array[2]", new Value.Text( "Third" ) );

        assertEquals( "First", rootDataSet.getData( "array" ).getObject() );
        assertEquals( "First", rootDataSet.getData( "array[0]" ).getObject() );

        assertEquals( "Second", rootDataSet.getData( "array[1]" ).getObject() );

        assertEquals( "Third", rootDataSet.getData( "array[2]" ).getObject() );
    }

    @Test
    public void array_overwriting_does_not_create_array()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setData( "noArray", new Value.Text( "First" ) );
        rootDataSet.setData( "noArray", new Value.Text( "Second" ) );
        rootDataSet.setData( "noArray", new Value.Text( "Third" ) );

        assertEquals( "Third", rootDataSet.getData( "noArray" ).getObject() );
    }

    @Test
    public void array_setData_assigning_same_array_element_a_second_time_ovewrites_the_first_value()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setData( "array[0]", new Value.Text( "First" ) );
        rootDataSet.setData( "array[1]", new Value.Text( "Second" ) );
        rootDataSet.setData( "array[1]", new Value.Text( "Second again" ) );

        assertEquals( "First", rootDataSet.getData( "array[0]" ).getObject() );
        assertEquals( "Second again", rootDataSet.getData( "array[1]" ).getObject() );
        assertNull( rootDataSet.getData( "array[2]" ) );
    }

    @Test
    public void array_setData_setting_second_data_with_same_path_at_index_1_creates_array_of_size_2()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setData( "myArray", new Value.Text( "First" ) );
        rootDataSet.setData( "myArray[1]", new Value.Text( "Second" ) );

        assertEquals( true, rootDataSet.getData( "myArray" ).isArray() );
        assertEquals( 2, rootDataSet.getData( "myArray" ).getArray().size() );
        assertEquals( "First", rootDataSet.getData( "myArray[0]" ).getObject() );
        assertEquals( "Second", rootDataSet.getData( "myArray[1]" ).getObject() );
        assertEquals( "myArray[0]", rootDataSet.getData( "myArray" ).getPath().toString() );
    }

    @Test
    public void array_setData_array_within_set()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setData( "set.myArray[0]", new Value.Text( "First" ) );
        rootDataSet.setData( "set.myArray[1]", new Value.Text( "Second" ) );

        assertEquals( "First", rootDataSet.getData( "set.myArray[0]" ).getObject() );
        assertEquals( "Second", rootDataSet.getData( "set.myArray[1]" ).getObject() );
        assertEquals( "set.myArray[0]", rootDataSet.getData( "set.myArray" ).getPath().toString() );
        assertEquals( "First", rootDataSet.getData( "set.myArray" ).getString( 0 ) );
        assertEquals( "Second", rootDataSet.getData( "set.myArray" ).getString( 1 ) );
    }

    @Test
    public void array_setData_array_of_set_within_set()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setData( "company.address[0].street", new Value.Text( "Kirkegata 1-3" ) );
        rootDataSet.setData( "company.address[1].street", new Value.Text( "Sonsteli" ) );

        assertEquals( "Kirkegata 1-3", rootDataSet.getData( "company.address[0].street" ).getString() );
        assertEquals( "Sonsteli", rootDataSet.getData( "company.address[1].street" ).getString() );
        assertEquals( "Sonsteli", rootDataSet.getDataSet( "company" ).getDataSet( "address", 1 ).getData( "street" ).getString() );
        assertEquals( "Sonsteli", rootDataSet.getDataSet( "company" ).getDataSet( "address[1]" ).getData( "street" ).getString() );
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

        assertEquals( "Kirkegata 1-3", rootDataSet.getData( "company.address[0].street" ).getObject() );
        assertEquals( "Sonsteli", rootDataSet.getData( "company.address[1].street" ).getObject() );
        assertEquals( "Sonsteli", rootDataSet.getDataSet( "company" ).getDataSet( "address", 1 ).getData( "street" ).getString() );
        assertEquals( "Sonsteli", rootDataSet.getDataSet( "company" ).getDataSet( "address[1]" ).getData( "street" ).getString() );
    }

    @Test
    public void array_set()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setData( "set[0].myText", new Value.Text( "First" ) );
        rootDataSet.setData( "set[1].myText", new Value.Text( "Second" ) );

        assertEquals( "First", rootDataSet.getData( "set.myText" ).getObject() );
        assertEquals( "First", rootDataSet.getData( "set[0].myText" ).getObject() );
        assertEquals( "Second", rootDataSet.getData( "set[1].myText" ).getObject() );
        assertEquals( true, rootDataSet.getDataSet( "set" ).isArray() );
        assertEquals( 0, rootDataSet.getDataSet( "set[0]" ).getArrayIndex() );
        assertEquals( "set[0]", rootDataSet.getDataSet( "set[0]" ).getPath().toString() );
        assertEquals( "set[0].myText", rootDataSet.getData( "set[0].myText" ).getPath().toString() );
        assertEquals( "set[1].myText", rootDataSet.getData( "set[1].myText" ).getPath().toString() );
        assertEquals( "set[0]", rootDataSet.getDataSet( "set[0]" ).getPath().toString() );
        assertEquals( "set[1]", rootDataSet.getDataSet( "set[1]" ).getPath().toString() );
        assertEquals( "First", rootDataSet.getDataSet( "set[0]" ).getData( "myText" ).getString() );
        assertEquals( "Second", rootDataSet.getDataSet( "set[1]" ).getData( "myText" ).getString() );
    }

    @Test
    public void array_set2()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setData( "set[0].myText", new Value.Text( "First" ) );
        rootDataSet.setData( "set[0].myOther", new Value.Text( "First other" ) );
        rootDataSet.setData( "set[1].myText", new Value.Text( "Second" ) );
        rootDataSet.setData( "set[1].myOther", new Value.Text( "Second other" ) );

        assertEquals( "First", rootDataSet.getData( "set[0].myText" ).getObject() );
        assertEquals( "First other", rootDataSet.getData( "set[0].myOther" ).getObject() );
        assertEquals( "Second", rootDataSet.getData( "set[1].myText" ).getObject() );
        assertEquals( "Second other", rootDataSet.getData( "set[1].myOther" ).getObject() );
        assertEquals( "set[0].myText", rootDataSet.getData( "set[0].myText" ).getPath().toString() );
        assertEquals( "set[1].myText", rootDataSet.getData( "set[1].myText" ).getPath().toString() );
        assertEquals( "set[0]", rootDataSet.getDataSet( "set[0]" ).getPath().toString() );
        assertEquals( "set[1]", rootDataSet.getDataSet( "set[1]" ).getPath().toString() );
        assertEquals( "First", rootDataSet.getEntry( "set[0]" ).toDataSet().getData( "myText" ).getString() );
        assertEquals( "Second", rootDataSet.getEntry( "set[1]" ).toDataSet().getData( "myText" ).getString() );
    }

    @Test
    public void tags()
    {
        contentType.form().addFormItem( newInput().name( "myTags" ).inputType( InputTypes.TAGS ).build() );

        // TODO: Are'nt tags best stored as an array? A global mixin multiple textline?
        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getRootDataSet().setData( "myTags", new Value.Text( "A line of text" ) );

        assertEquals( "A line of text", content.getRootDataSet().getData( "myTags" ).getObject() );
    }

    @Test
    public void phone()
    {
        contentType.form().addFormItem( newInput().name( "myPhone" ).inputType( InputTypes.PHONE ).required( true ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getRootDataSet().setData( "myPhone", new Value.Text( "98327891" ) );

        assertEquals( "98327891", content.getRootDataSet().getData( "myPhone" ).getObject() );
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
        rootDataSet.setData( "name", new Value.Text( "Ola Nordmann" ) );
        rootDataSet.setData( "personalia.eyeColour", new Value.Text( "Blue" ) );
        rootDataSet.setData( "personalia.hairColour", new Value.Text( "Blonde" ) );

        assertEquals( "Ola Nordmann", rootDataSet.getData( "name" ).getObject() );
        assertEquals( "Blue", rootDataSet.getData( "personalia.eyeColour" ).getObject() );
        assertEquals( "Blonde", rootDataSet.getData( "personalia.hairColour" ).getObject() );
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
        rootDataSet.setData( "name", new Value.Text( "Norske" ) );
        rootDataSet.setData( "personalia[0].name", new Value.Text( "Ola Nordmann" ) );
        rootDataSet.setData( "personalia[0].eyeColour", new Value.Text( "Blue" ) );
        rootDataSet.setData( "personalia[0].hairColour", new Value.Text( "Blonde" ) );
        rootDataSet.setData( "personalia[1].name", new Value.Text( "Kari Trestakk" ) );
        rootDataSet.setData( "personalia[1].eyeColour", new Value.Text( "Green" ) );
        rootDataSet.setData( "personalia[1].hairColour", new Value.Text( "Brown" ) );

        assertEquals( "Norske", rootDataSet.getData( "name" ).getObject() );
        assertEquals( "Ola Nordmann", rootDataSet.getData( "personalia[0].name" ).getObject() );
        assertEquals( "Blue", rootDataSet.getData( "personalia[0].eyeColour" ).getObject() );
        assertEquals( "Blonde", rootDataSet.getData( "personalia[0].hairColour" ).getObject() );
        assertEquals( "Kari Trestakk", rootDataSet.getData( "personalia[1].name" ).getObject() );
        assertEquals( "Green", rootDataSet.getData( "personalia[1].eyeColour" ).getObject() );
        assertEquals( "Brown", rootDataSet.getData( "personalia[1].hairColour" ).getObject() );
    }

    @Test
    public void unstructured()
    {
        Content content = newContent().build();
        RootDataSet rootDataSet = content.getRootDataSet();
        rootDataSet.setData( "firstName", new Value.Text( "Thomas" ) );
        rootDataSet.setData( "description", new Value.HtmlPart( "Grew up in Noetteveien" ) );
        rootDataSet.setData( "child[0].name", new Value.Text( "Joachim" ) );
        rootDataSet.setData( "child[0].age", new Value.Text( "9" ) );
        rootDataSet.setData( "child[0].features.eyeColour", new Value.Text( "Blue" ) );
        rootDataSet.setData( "child[0].features.hairColour", new Value.Text( "Blonde" ) );
        rootDataSet.setData( "child[1].name", new Value.Text( "Madeleine" ) );
        rootDataSet.setData( "child[1].age", new Value.Text( "7" ) );
        rootDataSet.setData( "child[1].features.eyeColour", new Value.Text( "Brown" ) );
        rootDataSet.setData( "child[1].features.hairColour", new Value.Text( "Black" ) );

        assertEquals( "Thomas", rootDataSet.getData( "firstName" ).getObject() );
        assertEquals( DataTypes.TEXT, rootDataSet.getData( "firstName" ).getType() );
        assertEquals( DataTypes.HTML_PART, rootDataSet.getData( "description" ).getType() );
        assertEquals( "Joachim", rootDataSet.getData( "child[0].name" ).getObject() );
        assertEquals( "9", rootDataSet.getData( "child[0].age" ).getObject() );
        assertEquals( "Blue", rootDataSet.getData( "child[0].features.eyeColour" ).getObject() );
        assertEquals( "Blonde", rootDataSet.getData( "child[0].features.hairColour" ).getObject() );
        assertEquals( "Madeleine", rootDataSet.getData( "child[1].name" ).getObject() );
        assertEquals( "7", rootDataSet.getData( "child[1].age" ).getObject() );
        assertEquals( "Brown", rootDataSet.getData( "child[1].features.eyeColour" ).getObject() );
        assertEquals( "Black", rootDataSet.getData( "child[1].features.hairColour" ).getObject() );
    }

    @Test
    public void unstructured_getEntries()
    {
        Content content = newContent().build();
        RootDataSet rootDataSet = content.getRootDataSet();
        rootDataSet.setData( "child[0].name", new Value.Text( "Joachim" ) );
        rootDataSet.setData( "child[0].age", new Value.Text( "9" ) );
        rootDataSet.setData( "child[0].features.eyeColour", new Value.Text( "Blue" ) );
        rootDataSet.setData( "child[0].features.hairColour", new Value.Text( "Blonde" ) );
        rootDataSet.setData( "child[1].name", new Value.Text( "Madeleine" ) );
        rootDataSet.setData( "child[1].age", new Value.Text( "7" ) );
        rootDataSet.setData( "child[1].features.eyeColour", new Value.Text( "Brown" ) );
        rootDataSet.setData( "child[1].features.hairColour", new Value.Text( "Black" ) );

        DataSet child0 = rootDataSet.getEntry( "child[0]" ).toDataSet();
        assertEquals( "Joachim", child0.getData( "name" ).getObject() );
        assertEquals( "9", child0.getData( "age" ).getObject() );
        assertEquals( "Blue", child0.getData( "features.eyeColour" ).getObject() );

        DataSet child1 = rootDataSet.getEntry( "child[1]" ).toDataSet();
        assertEquals( "Madeleine", child1.getData( "name" ).getObject() );
        assertEquals( "7", child1.getData( "age" ).getObject() );
        assertEquals( "Brown", child1.getData( "features.eyeColour" ).getObject() );
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
        rootDataSet.setData( "child[0].name", new Value.Text( "Joachim" ) );
        rootDataSet.setData( "child[0].age", new Value.Text( "9" ) );
        rootDataSet.setData( "child[0].features.eyeColour", new Value.Text( "Blue" ) );
        rootDataSet.setData( "child[0].features.hairColour", new Value.Text( "Blonde" ) );
        rootDataSet.setData( "child[1].name", new Value.Text( "Madeleine" ) );
        rootDataSet.setData( "child[1].age", new Value.Text( "7" ) );
        rootDataSet.setData( "child[1].features.eyeColour", new Value.Text( "Brown" ) );
        rootDataSet.setData( "child[1].features.hairColour", new Value.Text( "Black" ) );

        DataSet child0 = rootDataSet.getEntry( "child[0]" ).toDataSet();
        assertEquals( "Joachim", child0.getData( "name" ).getObject() );
        assertEquals( "9", child0.getData( "age" ).getObject() );
        assertEquals( "Blue", child0.getData( "features.eyeColour" ).getObject() );

        DataSet child1 = rootDataSet.getEntry( "child[1]" ).toDataSet();
        assertEquals( "Madeleine", child1.getData( "name" ).getObject() );
        assertEquals( "7", child1.getData( "age" ).getObject() );
        assertEquals( "Brown", child1.getData( "features.eyeColour" ).getObject() );
    }

    @Test
    public void given_unstructured_content_when_getting_values_then_they_are_returned()
    {
        // setup
        Content content = newContent().build();
        RootDataSet rootDataSet = content.getRootDataSet();
        rootDataSet.setData( "name", new Value.Text( "Thomas" ) );
        rootDataSet.setData( "personalia.eyeColour", new Value.Text( "Blue" ) );
        rootDataSet.setData( "personalia.hairColour", new Value.Text( "Blonde" ) );

        assertEquals( DataTypes.TEXT, rootDataSet.getData( "personalia.eyeColour" ).getType() );
        assertEquals( "Blue", rootDataSet.getData( "personalia.eyeColour" ).getObject() );
        assertEquals( "personalia.eyeColour", rootDataSet.getData( "personalia.eyeColour" ).getPath().toString() );
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
        rootDataSet.setData( "name", new Value.Text( "Ola Norman" ) );
        rootDataSet.setData( "eyeColour", new Value.Text( "Blue" ) );
        rootDataSet.setData( "hairColour", new Value.Text( "Blonde" ) );
        rootDataSet.setData( "tattoo[0]", new Value.Text( "Skull on left arm" ) );
        rootDataSet.setData( "tattoo[1]", new Value.Text( "Mothers name on right arm" ) );
        rootDataSet.setData( "scar[0]", new Value.Text( "Chin" ) );

        // verify
        assertEquals( "Ola Norman", rootDataSet.getData( "name" ).getString() );
        assertEquals( "Blue", rootDataSet.getData( "eyeColour" ).getString() );
        assertEquals( "Blonde", rootDataSet.getData( "hairColour" ).getString() );
        assertEquals( "Skull on left arm", rootDataSet.getData( "tattoo[0]" ).getString() );
        assertEquals( "Mothers name on right arm", rootDataSet.getData( "tattoo[1]" ).getString() );
        assertEquals( "Chin", rootDataSet.getData( "scar[0]" ).getString() );
    }

    @Test
    public void given_array_when_setting_data_of_another_type_to_array_then_exception_is_thrown()
    {
        // setup
        Content content = newContent().build();
        content.getRootDataSet().setData( "myData", new Value.Text( "Value 1" ) );

        // exercise
        try
        {
            content.getRootDataSet().setData( "myData[1]", new Value.Date( new DateMidnight( 2000, 1, 1 ) ) );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Array [myData] expects Data of type [Text]. Data [myData] was of type: DateMidnight", e.getMessage() );
        }
    }

    @Test
    public void new_way()
    {
        RootDataSet rootDataSet = DataSet.newRootDataSet();
        rootDataSet.add( newData().type( DataTypes.TEXT ).name( "myData" ).value( "1" ).build() );
        rootDataSet.add( newText().name( "myData" ).value( "1" ).build() );
        rootDataSet.add( newXml().name( "myXml" ).value( "<root/>" ).build() );

        assertEquals( "1", rootDataSet.getData( "myData" ).getValue().asString() );
        assertEquals( "1", rootDataSet.getData( "myData" ).getString() );
    }

    @Test
    public void new_way2()
    {
        RootDataSet rootDataSet = DataSet.newRootDataSet();

        rootDataSet.add( new Data.Text( "myData", "1" ) );
        rootDataSet.add( new Data.Text( "myArray", "1" ) );
        rootDataSet.add( new Data.Text( "myArray", "2" ) );
        //rootDataSet.add( new Xml( "myXml", "<root></root>" ) );

        Content content = newContent().name( "myContent" ).rootDataSet( rootDataSet ).build();

        assertEquals( "1", rootDataSet.getData( "myArray" ).getObject() );
        assertEquals( "1", rootDataSet.getData( "myArray", 0 ).getObject() );
        assertEquals( "2", rootDataSet.getData( "myArray[1]" ).getObject() );
        assertEquals( true, rootDataSet.getData( "myArray[1]" ).isArray() );
        assertEquals( true, rootDataSet.getData( "myArray" ).isArray() );
        assertEquals( false, rootDataSet.getData( "myData" ).isArray() );
        assertEquals( 0, rootDataSet.getData( "myData" ).getArrayIndex() );
        assertEquals( 0, rootDataSet.getData( "myArray" ).getArrayIndex() );
        assertEquals( 1, rootDataSet.getData( "myArray[1]" ).getArrayIndex() );
        assertEquals( 2, rootDataSet.entryCount( "myArray" ) );
        assertEquals( 1, rootDataSet.entryCount( "myData" ) );

        Data myArray = content.getRootDataSet().getData( "myArray" );
    }
}
