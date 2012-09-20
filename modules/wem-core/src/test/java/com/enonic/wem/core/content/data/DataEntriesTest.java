package com.enonic.wem.core.content.data;


import org.junit.Test;

import com.enonic.wem.core.content.datatype.DataTypes;

import static org.junit.Assert.*;

public class DataEntriesTest
{
    @Test
    public void given_added_two_data_with_same_path_when_size_then_two_is_returned()
    {
        DataEntries dataEntries = new DataEntries();
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 1" ).build() );
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 2" ).build() );

        assertEquals( 2, dataEntries.size() );
    }

    @Test
    public void given_added_two_data_with_same_path_when_get_index_0_then_first_added_value_is_returned()
    {
        DataEntries dataEntries = new DataEntries();
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 1" ).build() );
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 2" ).build() );

        assertEquals( "Value 1", dataEntries.get( new EntryPath( "myComponent[0]" ).getLastElement() ).getValue() );
    }

    @Test
    public void given_added_two_data_with_same_path_when_get_index_1_then_second_added_value_is_returned()
    {
        DataEntries dataEntries = new DataEntries();
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 1" ).build() );
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 2" ).build() );

        assertEquals( "Value 2", dataEntries.get( new EntryPath( "myComponent[1]" ).getLastElement() ).getValue() );
        assertEquals( 0, dataEntries.get( new EntryPath( "myComponent[0]" ).getLastElement() ).getPath().getLastElement().getIndex() );
        assertEquals( 1, dataEntries.get( new EntryPath( "myComponent[1]" ).getLastElement() ).getPath().getLastElement().getIndex() );
    }

    @Test
    public void given_existing_entries_at_path_when_getting_entry_at_index_out_of_bounds_then_null_is_returned()
    {
        DataEntries dataEntries = new DataEntries();
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 1" ).build() );
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 2" ).build() );

        assertNull( dataEntries.get( new EntryPath( "myComponent[2]" ).getLastElement() ) );

    }

    @Test
    public void given_one_existing_data_when_adding_data_with_same_path_then_existing_and_new_data_becomes_indexed()
    {
        DataEntries dataEntries = new DataEntries();
        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 1" ).build() );
        assertEquals( "myComponent", dataEntries.get( new EntryPath( "myComponent" ).getLastElement() ).getPath().toString() );
        assertEquals( "Value 1", dataEntries.get( new EntryPath( "myComponent" ).getLastElement() ).getValue() );

        dataEntries.add( Data.newData().path( new EntryPath( "myComponent" ) ).type( DataTypes.TEXT ).value( "Value 2" ).build() );

        assertEquals( "myComponent[0]", dataEntries.get( new EntryPath( "myComponent" ).getLastElement() ).getPath().toString() );
        assertEquals( "Value 1", dataEntries.get( new EntryPath( "myComponent" ).getLastElement() ).getValue() );

        assertEquals( "myComponent[1]", dataEntries.get( new EntryPath( "myComponent[1]" ).getLastElement() ).getPath().toString() );
        assertEquals( "Value 2", dataEntries.get( new EntryPath( "myComponent[1]" ).getLastElement() ).getValue() );

        assertEquals( "myComponent[0]", dataEntries.get( new EntryPath( "myComponent[0]" ).getLastElement() ).getPath().toString() );
        assertEquals( "Value 1", dataEntries.get( new EntryPath( "myComponent[0]" ).getLastElement() ).getValue() );
    }
}
