package com.enonic.wem.api.content.data;


import org.joda.time.DateMidnight;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.api.content.datatype.DataTypes;

import static org.junit.Assert.*;

public class DataEntriesTest
{
    @Test
    public void given_added_two_data_with_same_path_when_size_then_two_is_returned()
    {
        // setup
        DataEntries dataEntries = new DataEntries();
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 1" ).build() );
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 2" ).build() );

        // exercise and verify
        assertEquals( 2, dataEntries.size() );
    }

    @Test
    public void given_added_two_data_with_same_path_when_get_index_0_then_first_added_value_is_returned()
    {
        // setup
        DataEntries dataEntries = new DataEntries();
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 1" ).build() );
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 2" ).build() );

        // exercise and verify
        assertEquals( "Value 1", dataEntries.get( new EntryPath( "myComponent[0]" ).getLastElement() ).getValue() );
    }

    @Test
    public void given_added_two_data_with_same_path_when_get_index_1_then_second_added_value_is_returned()
    {
        // setup
        DataEntries dataEntries = new DataEntries();
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 1" ).build() );
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 2" ).build() );

        // exercise and verify
        assertEquals( "Value 2", dataEntries.get( new EntryPath( "myComponent[1]" ).getLastElement() ).getValue() );
        assertEquals( 0, dataEntries.get( new EntryPath( "myComponent[0]" ).getLastElement() ).getPath().getLastElement().getIndex() );
        assertEquals( 1, dataEntries.get( new EntryPath( "myComponent[1]" ).getLastElement() ).getPath().getLastElement().getIndex() );
    }

    @Test
    public void given_existing_entries_at_path_when_getting_entry_at_index_out_of_bounds_then_null_is_returned()
    {
        // setup
        DataEntries dataEntries = new DataEntries();
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 1" ).build() );
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 2" ).build() );

        // exercise and verify
        Assert.assertNull( dataEntries.get( new EntryPath( "myComponent[2]" ).getLastElement() ) );
    }

    @Test
    public void given_one_existing_data_when_adding_data_with_same_path_then_existing_and_new_data_becomes_indexed()
    {
        // setup
        DataEntries dataEntries = new DataEntries();
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 1" ).build() );
        assertEquals( "myComponent", dataEntries.get( new EntryPath( "myComponent" ).getLastElement() ).getPath().toString() );
        assertEquals( "Value 1", dataEntries.get( new EntryPath( "myComponent" ).getLastElement() ).getValue() );

        // exercise
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 2" ).build() );

        // verify
        assertEquals( "myComponent[0]", dataEntries.get( new EntryPath( "myComponent" ).getLastElement() ).getPath().toString() );
        assertEquals( "Value 1", dataEntries.get( new EntryPath( "myComponent" ).getLastElement() ).getValue() );

        assertEquals( "myComponent[1]", dataEntries.get( new EntryPath( "myComponent[1]" ).getLastElement() ).getPath().toString() );
        assertEquals( "Value 2", dataEntries.get( new EntryPath( "myComponent[1]" ).getLastElement() ).getValue() );

        assertEquals( "myComponent[0]", dataEntries.get( new EntryPath( "myComponent[0]" ).getLastElement() ).getPath().toString() );
        assertEquals( "Value 1", dataEntries.get( new EntryPath( "myComponent[0]" ).getLastElement() ).getValue() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_existing_array_when_adding_data_to_array_of_different_type_then_exception_is_thrown()
    {
        // setup
        DataEntries dataEntries = new DataEntries();
        dataEntries.add( Data.newData().path( new EntryPath( "myData.myArray" ) ).value( "Value" ).type( DataTypes.TEXT ).build() );

        // exercise
        dataEntries.add( Data.newData().path( new EntryPath( "myData.myArray" ) ).value( new DateMidnight( 2000, 1, 1 ) ).type(
            DataTypes.DATE ).build() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_existing_array_when_setting_data_to_array_of_different_type_then_exception_is_thrown()
    {
        // setup
        DataEntries dataEntries = new DataEntries();
        dataEntries.setData( new EntryPath( "myArray[0]" ).getLastElement(),
                             Data.newData().path( new EntryPath( "myArray" ) ).value( "Value" ).type( DataTypes.TEXT ).build() );

        // exercise
        dataEntries.setData( new EntryPath( "myArray[1]" ).getLastElement(),
                             Data.newData().path( new EntryPath( "myArray" ) ).value( new DateMidnight( 2000, 1, 1 ) ).type(
                                 DataTypes.DATE ).build() );
    }
}
