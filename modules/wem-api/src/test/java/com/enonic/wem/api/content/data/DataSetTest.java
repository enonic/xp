package com.enonic.wem.api.content.data;


import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.FormItems;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;

import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static org.junit.Assert.*;

public class DataSetTest
{
    @Test
    public void setValue_when_given_path_does_not_exists()
    {
        FormItems formItems = new FormItems();
        FormItemSet formItemSet = newFormItemSet().name( "personalia" ).multiple( true ).build();
        formItemSet.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        formItems.add( formItemSet );

        DataSet dataSet = new DataSet( new EntryPath() );

        try
        {
            dataSet.setData( new EntryPath( "unknown.eyeColour" ), "Brown", DataTypes.TEXT );
        }
        catch ( Exception e )
        {
            Assert.assertTrue( e instanceof IllegalArgumentException );
            Assert.assertTrue( e.getMessage().startsWith( "No FormItem found at: unknown.eyeColour" ) );
        }
    }

    @Test
    public void getValue_when_having_sub_type()
    {
        FormItemSet formItemSet = newFormItemSet().name( "personalia" ).multiple( false ).build();
        formItemSet.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );
        FormItems formItems = new FormItems();
        formItems.add( formItemSet );

        DataSet dataSet = new DataSet( new EntryPath() );
        dataSet.setData( new EntryPath( "personalia.eyeColour" ), "Brown", DataTypes.TEXT );
        dataSet.setData( new EntryPath( "personalia.hairColour" ), "Brown", DataTypes.TEXT );

        assertEquals( "Brown", dataSet.getData( "personalia.eyeColour" ).getValue() );
        assertEquals( "Brown", dataSet.getData( "personalia.hairColour" ).getValue() );
    }

    @Test
    public void getValue_when_having_multiple_sub_type_in_single_sub_type()
    {
        FormItemSet personalia = newFormItemSet().name( "personalia" ).label( "Personalia" ).multiple( true ).build();
        FormItemSet crimes = newFormItemSet().name( "crimes" ).multiple( true ).build();
        crimes.add( newInput().name( "description" ).type( InputTypes.TEXT_LINE ).build() );
        crimes.add( newInput().name( "year" ).type( InputTypes.TEXT_LINE ).build() );
        personalia.add( crimes );
        FormItems formItems = new FormItems();
        formItems.add( personalia );

        DataSet dataSet = new DataSet( new EntryPath() );
        dataSet.setData( new EntryPath( "personalia.crimes[0].description" ), "Stole purse from old lady.", DataTypes.TEXT );
        dataSet.setData( new EntryPath( "personalia.crimes[0].year" ), "2011", DataTypes.TEXT );
        dataSet.setData( new EntryPath( "personalia.crimes[1].description" ), "Drove car in 80 in 50 zone.", DataTypes.TEXT );
        dataSet.setData( new EntryPath( "personalia.crimes[1].year" ), "2012", DataTypes.TEXT );

        assertEquals( "Stole purse from old lady.", dataSet.getData( "personalia.crimes[0].description" ).getValue() );
        assertEquals( "2011", dataSet.getData( "personalia.crimes[0].year" ).getValue() );
        assertEquals( "Drove car in 80 in 50 zone.", dataSet.getData( "personalia.crimes[1].description" ).getValue() );
        assertEquals( "2012", dataSet.getData( "personalia.crimes[1].year" ).getValue() );
    }

    @Test
    public void getValue_when_having_multiple_sub_type()
    {
        FormItems formItems = new FormItems();
        FormItemSet formItemSet = newFormItemSet().name( "persons" ).multiple( true ).build();
        formItemSet.add( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        formItems.add( formItemSet );

        DataSet dataSet = new DataSet( new EntryPath() );
        dataSet.setData( new EntryPath( "persons[0].name" ), "Arn", DataTypes.TEXT );
        dataSet.setData( new EntryPath( "persons[0].eyeColour" ), "Brown", DataTypes.TEXT );

        assertEquals( "Arn", dataSet.getData( "persons[0].name" ).getValue() );
        assertEquals( "Brown", dataSet.getData( "persons[0].eyeColour" ).getValue() );
    }

    @Test
    public void given_two_data_added_with_same_path_then_array_is_created()
    {
        DataSet dataSet = new DataSet( new EntryPath() );
        dataSet.add( Data.newData().path( new EntryPath( "myArray" ) ).value( "1" ).type( DataTypes.TEXT ).build() );
        dataSet.add( Data.newData().path( new EntryPath( "myArray" ) ).value( "2" ).type( DataTypes.TEXT ).build() );

        assertEquals( "1", dataSet.getData( "myArray[0]" ).getValue() );
        assertEquals( "2", dataSet.getData( "myArray[1]" ).getValue() );

        assertEquals( "myArray[0]", dataSet.getData( "myArray[0]" ).getPath().toString() );
    }

    @Test
    public void given_second_data_with_same_path_but_indexed_then_array_is_created()
    {
        DataSet dataSet = new DataSet( new EntryPath() );
        dataSet.add( Data.newData().path( new EntryPath( "myArray" ) ).value( "1" ).type( DataTypes.TEXT ).build() );
        dataSet.add( Data.newData().path( new EntryPath( "myArray[1]" ) ).value( "2" ).type( DataTypes.TEXT ).build() );

        assertEquals( "1", dataSet.getData( "myArray[0]" ).getValue() );
        assertEquals( "2", dataSet.getData( "myArray[1]" ).getValue() );

        assertEquals( "myArray[0]", dataSet.getData( "myArray[0]" ).getPath().toString() );
    }

    @Test
    public void given_Data_not_parent_of_DataSet_when_adding_it_then_exception_is_thrown()
    {
        DataSet dataSet = new DataSet( new EntryPath( "parent" ) );
        try
        {
            dataSet.add( Data.newData().path( new EntryPath( "otherParent.myArray" ) ).value( "1" ).type( DataTypes.TEXT ).build() );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals(
                "This DataSet [parent] is not the parent of the Data to be added: Data{path=otherParent.myArray, type=Text, value=1}",
                e.getMessage() );
        }
    }

    @Test
    public void given_xxx2()
    {
        DataSet dataSet = new DataSet( new EntryPath() );
        dataSet.add( Data.newData().path( new EntryPath( "myArray" ) ).value( "1" ).type( DataTypes.TEXT ).build() );
        dataSet.setData( new EntryPath( "myArray[1]" ), "2", DataTypes.TEXT );

        assertEquals( "1", dataSet.getData( "myArray[0]" ).getValue() );
        assertEquals( "2", dataSet.getData( "myArray[1]" ).getValue() );

        assertEquals( "myArray[0]", dataSet.getData( "myArray[0]" ).getPath().toString() );
    }
}

