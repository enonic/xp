package com.enonic.wem.api.content.data;


import java.util.Iterator;

import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.api.content.datatype.DataTypes;

import static com.enonic.wem.api.content.data.Data.newData;
import static org.junit.Assert.*;

public class DataEntriesTest
{
    @Test
    public void given_added_two_data_with_same_path_when_size_then_one_is_returned()
    {
        // setup
        DataEntries dataEntries = new DataEntries();
        dataEntries.add( newData().path( new EntryPath( "myInput" ) ).type( DataTypes.TEXT ).value( "Value 1" ).build() );
        dataEntries.add( newData().path( new EntryPath( "myInput" ) ).type( DataTypes.TEXT ).value( "Value 2" ).build() );

        // exercise and verify
        assertEquals( 1, dataEntries.size() );
    }

    @Test
    public void given_adding_one_and_set_one_with_same_path_when_size_then_one_is_returned()
    {
        // setup
        DataEntries dataEntries = new DataEntries();
        dataEntries.add( newData().path( new EntryPath( "myInput" ) ).type( DataTypes.TEXT ).value( "Initial" ).build() );
        dataEntries.setData( new EntryPath( "myInput[0]" ), "Changed", DataTypes.TEXT );

        // exercise and verify
        assertEquals( 1, dataEntries.size() );
    }

    @Test
    public void given_adding_one_and_set_one_with_same_path_when_iterated_then_array_is_returned_with_one_data_containing_last_set_value()
    {
        // setup
        DataEntries dataEntries = new DataEntries();
        dataEntries.add( newData().path( new EntryPath( "myInput" ) ).type( DataTypes.TEXT ).value( "Initial" ).build() );
        dataEntries.setData( new EntryPath( "myInput[0]" ), "Changed", DataTypes.TEXT );

        // exercise and verify
        Iterator<Data> iterator = dataEntries.iterator();
        Data data = iterator.next();
        assertEquals( 1, data.getDataArray().size() );
        assertEquals( "Changed", data.getDataArray().getData( 0 ).getString() );

        assertFalse( iterator.hasNext() );
    }

    @Test
    //@Ignore
    public void given_added_two_data_with_same_path_when_get_index_1_then_second_added_value_is_returned()
    {
        // setup
        DataEntries dataEntries = new DataEntries();
        dataEntries.add( newData().path( new EntryPath( "myInput" ) ).type( DataTypes.TEXT ).value( "Value 1" ).build() );
        dataEntries.add( newData().path( new EntryPath( "myInput" ) ).type( DataTypes.TEXT ).value( "Value 2" ).build() );

        // exercise and verify
        Data myInput = dataEntries.get( new EntryPath.Element( "myInput" ) );

        assertEquals( "Value 2", myInput.getDataArray().getData( 1 ).getValue() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_existing_array_when_adding_data_to_array_of_different_type_then_exception_is_thrown()
    {
        // setup
        DataEntries dataEntries = new DataEntries();
        dataEntries.add( newData().path( new EntryPath( "myData.myArray[0]" ) ).value( "Value" ).type( DataTypes.TEXT ).build() );

        // exercise
        dataEntries.add(
            newData().path( new EntryPath( "myData.myArray[1]" ) ).value( new DateMidnight( 2000, 1, 1 ) ).type( DataTypes.DATE ).build() );
    }
}
