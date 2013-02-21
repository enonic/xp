package com.enonic.wem.api.content.data;


import java.util.Iterator;

import org.junit.Test;

import com.enonic.wem.api.content.data.type.DataTypes;

import static org.junit.Assert.*;

public class DataSetTest
{
    @Test
    public void name_cannot_be_blank()
    {
        try
        {
            DataSet.newDataSet().name( " " ).build();
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "A name cannot be blank:  ", e.getMessage() );
        }
    }

    @Test
    public void add()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "A value" ).build() );

        assertEquals( "mySet.myData", dataSet.getData( "myData" ).getPath().toString() );
        assertEquals( "A value", dataSet.getData( "myData" ).getString() );
    }

    @Test
    public void add_more()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "1" ).build() );
        dataSet.add( Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "2" ).build() );

        assertEquals( "1", dataSet.getData( "myData" ).getString() );
        assertEquals( "1", dataSet.getData( "myData[0]" ).getString() );
        assertEquals( "2", dataSet.getData( "myData[1]" ).getString() );
    }

    @Test
    public void add_given_data_of_type_text_when_adding_data_of_other_type_with_same_name_then_exception_is_thrown()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "A value" ).build() );

        // exercise
        try
        {
            dataSet.add( Data.newData().name( "myData" ).type( DataTypes.HTML_PART ).value( "A value" ).build() );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            // verify
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Array [mySet.myData] expects Data of type [Text]. Data [mySet.myData] was of type: HtmlPart", e.getMessage() );
        }
    }

    @Test
    public void size()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "1" ).build() );
        dataSet.add( Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "2" ).build() );

        assertEquals( 2, dataSet.size() );
    }

    @Test
    public void entryCount()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "1" ).build() );
        dataSet.add( Data.newData().name( "myOtherData" ).type( DataTypes.TEXT ).value( "A" ).build() );
        dataSet.add( Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "2" ).build() );

        assertEquals( 2, dataSet.entryCount( "myData" ) );
    }

    @Test
    public void entryCount_given_non_existing_entry_then_0_is_returned()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "2" ).build() );

        assertEquals( 0, dataSet.entryCount( "nonExistingEntry" ) );
    }

    @Test
    public void add_given_two_data_added_with_same_path_then_array_is_created()
    {
        Data myArray1 = Data.newData().name( "myArray" ).value( "1" ).type( DataTypes.TEXT ).build();
        Data myArray2 = Data.newData().name( "myArray" ).value( "2" ).type( DataTypes.TEXT ).build();

        DataSet dataSet = DataSet.newRootDataSet();
        dataSet.add( myArray1 );
        dataSet.add( myArray2 );

        assertEquals( "1", dataSet.getData( "myArray[0]" ).getString() );
        assertEquals( "2", dataSet.getData( "myArray[1]" ).getString() );
    }

    @Test
    public void entryNames()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "1" ).build() );
        dataSet.add( Data.newData().name( "myOtherData" ).type( DataTypes.TEXT ).value( "A" ).build() );
        dataSet.add( Data.newData().name( "myData" ).type( DataTypes.TEXT ).value( "2" ).build() );

        Iterator<String> entryNames = dataSet.entryNames().iterator();
        assertEquals( "myData", entryNames.next() );
        assertEquals( "myOtherData", entryNames.next() );
        assertEquals( false, entryNames.hasNext() );
    }

    @Test
    public void getEntry()
    {
        DataSet dataSet = DataSet.newRootDataSet();
        dataSet.setData( EntryPath.from( "myData" ), "something", DataTypes.TEXT );

        assertNotNull( dataSet.getEntry( "myData" ) );
    }

    @Test
    public void getEntry_given_path_to_non_existing_entry_then_null_is_returned()
    {
        DataSet dataSet = DataSet.newRootDataSet();
        dataSet.setData( EntryPath.from( "myData" ), "something", DataTypes.TEXT );

        assertNull( dataSet.getEntry( "notExisting" ) );
    }

    @Test
    public void getDataSet_given_path_to_non_existing_DataSet_then_null_is_returned()
    {
        DataSet dataSet = DataSet.newRootDataSet();
        dataSet.setData( EntryPath.from( "mySet.myData" ), "something", DataTypes.TEXT );

        assertNull( dataSet.getDataSet( "notExisting" ) );
        assertNull( dataSet.getDataSet( "notExisting", 0 ) );
    }

    @Test
    public void getData_given_path_to_non_existing_data_then_null_is_returned()
    {
        DataSet dataSet = DataSet.newRootDataSet();
        dataSet.setData( EntryPath.from( "myData" ), "something", DataTypes.TEXT );

        assertNull( dataSet.getData( "notExisting" ) );
        assertNull( dataSet.getData( "notExisting", 0 ) );
    }

    @Test
    public void getData_given_array_when_getting_with_index_then_expected_data_is_returned()
    {
        DataSet dataSet = DataSet.newRootDataSet();
        dataSet.setData( EntryPath.from( "myArray[0]" ), "2a", DataTypes.TEXT );
        dataSet.setData( EntryPath.from( "myArray[1]" ), "2b", DataTypes.TEXT );

        assertEquals( "2a", dataSet.getData( "myArray", 0 ).getString() );
        assertEquals( "2b", dataSet.getData( "myArray", 1 ).getString() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getData_given_name_with_index_then_exception_is_thrown()
    {
        DataSet dataSet = DataSet.newRootDataSet();
        dataSet.setData( EntryPath.from( "myData" ), "1", DataTypes.TEXT );

        // exercise
        dataSet.getData( "myData[0]", 1 );
    }

    @Test
    public void getValue()
    {
        DataSet dataSet = DataSet.newRootDataSet();
        dataSet.add( Data.newData().name( "myData" ).value( "1" ).type( DataTypes.TEXT ).build() );

        assertEquals( "1", dataSet.getValue( "myData" ).getObject() );
        assertEquals( "1", dataSet.getValue( EntryPath.from( "myData" ) ).getObject() );
    }

    @Test
    public void getValue_when_having_array_of_set_within_single_set()
    {
        DataSet dataSet = DataSet.newRootDataSet();
        dataSet.setData( EntryPath.from( "personalia.crimes[0].description" ), "Stole purse from old lady.", DataTypes.TEXT );
        dataSet.setData( EntryPath.from( "personalia.crimes[0].year" ), "2011", DataTypes.TEXT );
        dataSet.setData( EntryPath.from( "personalia.crimes[1].description" ), "Drove car in 80 in 50 zone.", DataTypes.TEXT );
        dataSet.setData( EntryPath.from( "personalia.crimes[1].year" ), "2012", DataTypes.TEXT );

        assertEquals( "Stole purse from old lady.", dataSet.getData( "personalia.crimes[0].description" ).getObject() );
        assertEquals( "2011", dataSet.getData( "personalia.crimes[0].year" ).getObject() );
        assertEquals( "Drove car in 80 in 50 zone.", dataSet.getData( "personalia.crimes[1].description" ).getObject() );
        assertEquals( "2012", dataSet.getData( "personalia.crimes[1].year" ).getObject() );
    }

    @Test
    public void getValue_when_having_multiple_mixin()
    {
        DataSet dataSet = DataSet.newRootDataSet();
        dataSet.setData( EntryPath.from( "persons[0].name" ), "Arn", DataTypes.TEXT );
        dataSet.setData( EntryPath.from( "persons[0].eyeColour" ), "Brown", DataTypes.TEXT );

        assertEquals( "Arn", dataSet.getData( "persons[0].name" ).getObject() );
        assertEquals( "Brown", dataSet.getData( "persons[0].eyeColour" ).getObject() );
    }

    @Test
    public void getDataSet()
    {
        DataSet rootDataSet = DataSet.newRootDataSet();
        rootDataSet.add( DataSet.newDataSet().name( "mySet" ).build() );
        rootDataSet.add( DataSet.newDataSet().name( "myOtherSet" ).build() );

        assertEquals( "mySet", rootDataSet.getDataSet( "mySet" ).getPath().toString() );
        assertEquals( "mySet", rootDataSet.getDataSet( "mySet", 0 ).getPath().toString() );
        assertEquals( "mySet", rootDataSet.getDataSet( EntryPath.from( "mySet" ) ).getPath().toString() );
    }

    @Test
    public void setValue_root_set_with_one_entry()
    {
        DataSet dataSet = DataSet.newRootDataSet();
        dataSet.setData( EntryPath.from( "myData" ), "1", DataTypes.TEXT );

        assertEquals( "1", dataSet.getData( "myData" ).getString() );
        assertEquals( "1", dataSet.getData( "myData" ).getValue( 0 ).asString() );
        assertEquals( "1", dataSet.getValue( "myData" ).asString() );
    }

    @Test
    public void setValue_root_set_with_two_entries()
    {
        DataSet dataSet = DataSet.newRootDataSet();
        dataSet.setData( EntryPath.from( "myData1" ), "1", DataTypes.TEXT );
        dataSet.setData( EntryPath.from( "myData2" ), "2", DataTypes.TEXT );

        assertEquals( "1", dataSet.getData( "myData1" ).getString() );
        assertEquals( "2", dataSet.getData( "myData2" ).getString() );
    }

    @Test
    public void setValue_subSet_with_two_entries()
    {
        DataSet dataSet = DataSet.newRootDataSet();
        dataSet.setData( EntryPath.from( "set.myData1" ), "1", DataTypes.TEXT );
        dataSet.setData( EntryPath.from( "set.myData2" ), "2", DataTypes.TEXT );

        assertEquals( "1", dataSet.getData( "set.myData1" ).getString() );
        assertEquals( "2", dataSet.getValue( "set.myData2" ).asString() );
    }

    @Test
    public void setValue_given_one_data_added_and_a_second_data_with_same_name_set_at_index_one_then_array_is_created()
    {
        DataSet dataSet = DataSet.newRootDataSet();
        dataSet.add( Data.newData().name( "myArray" ).value( "1" ).type( DataTypes.TEXT ).build() );
        dataSet.setData( EntryPath.from( "myArray[1]" ), "2", DataTypes.TEXT );

        assertEquals( "1", dataSet.getValue( "myArray[0]" ).getObject() );
        assertEquals( "2", dataSet.getValue( "myArray[1]" ).getObject() );
    }

    @Test
    public void setValue_given_array_index_set_twice_then_value_of_last_is_returned()
    {
        DataSet dataSet = DataSet.newRootDataSet();
        dataSet.add( Data.newData().name( "myArray" ).value( "1" ).type( DataTypes.TEXT ).build() );

        // exercise
        dataSet.setData( EntryPath.from( "myArray[1]" ), "2a", DataTypes.TEXT );
        dataSet.setData( EntryPath.from( "myArray[1]" ), "2b", DataTypes.TEXT );

        // verify
        assertEquals( "2b", dataSet.getData( "myArray", 1 ).getString() );
    }

    @Test
    public void iterator_data_is_returned_in_inserted_order()
    {
        DataSet dataSet = DataSet.newRootDataSet();
        dataSet.setData( EntryPath.from( "myData1" ), "1", DataTypes.TEXT );
        dataSet.setData( EntryPath.from( "myArray[0]" ), "a", DataTypes.TEXT );
        dataSet.setData( EntryPath.from( "myData2" ), "2", DataTypes.TEXT );
        dataSet.setData( EntryPath.from( "myArray[1]" ), "b", DataTypes.TEXT );

        Iterator<Entry> it = dataSet.iterator();
        assertEquals( EntryId.from( "myData1", 0 ), it.next().getEntryId() );
        assertEquals( EntryId.from( "myArray", 0 ), it.next().getEntryId() );
        assertEquals( EntryId.from( "myData2", 0 ), it.next().getEntryId() );
        assertEquals( EntryId.from( "myArray", 1 ), it.next().getEntryId() );
    }

    @Test
    public void tostring_given_two_data()
    {
        DataSet dataSet = DataSet.newRootDataSet();
        dataSet.setData( EntryPath.from( "myData" ), "1", DataTypes.TEXT );
        dataSet.setData( EntryPath.from( "myOtherData" ), "2", DataTypes.TEXT );

        assertEquals( "{ myData, myOtherData }", dataSet.toString() );
    }

    @Test
    public void tostring_given_array()
    {
        DataSet rootSet = DataSet.newRootDataSet();
        rootSet.add( DataSet.newDataSet().name( "mySet" ).build() );

        DataSet mySet = DataSet.newDataSet().name( "mySet" ).build();
        mySet.setData( EntryPath.from( "myData[0]" ), "1", DataTypes.TEXT );
        mySet.setData( EntryPath.from( "myData[1]" ), "2", DataTypes.TEXT );
        rootSet.add( mySet );

        assertEquals( "mySet[1] { myData, myData[1] }", mySet.toString() );
    }

}

