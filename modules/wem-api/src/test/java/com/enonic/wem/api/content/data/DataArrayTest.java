package com.enonic.wem.api.content.data;


import org.junit.Test;

import com.enonic.wem.api.content.datatype.DataTypes;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class DataArrayTest
{
    @Test
    public void add_adding_one_data()
    {
        DataArray array = new DataArray( new EntryPath( "myArray" ), DataTypes.TEXT );

        array.add( Data.newData().path( new EntryPath( "myArray" ) ).type( DataTypes.TEXT ).value( "1" ).build() );

        assertEquals( 1, array.size() );
        assertEquals( new EntryPath( "myArray[0]" ), array.getData( 0 ).getPath() );
    }

    @Test
    public void add_adding_two_data()
    {
        DataArray array = new DataArray( new EntryPath( "myArray" ), DataTypes.TEXT );

        array.add( Data.newData().path( new EntryPath( "myArray" ) ).type( DataTypes.TEXT ).value( "1" ).build() );
        array.add( Data.newData().path( new EntryPath( "myArray" ) ).type( DataTypes.TEXT ).value( "2" ).build() );

        assertEquals( 2, array.size() );
        assertEquals( new EntryPath( "myArray[0]" ), array.getData( 0 ).getPath() );
        assertEquals( new EntryPath( "myArray[1]" ), array.getData( 1 ).getPath() );
    }

    @Test
    public void add_adding_first_setting_second()
    {
        DataArray array = new DataArray( new EntryPath( "myArray" ), DataTypes.TEXT );

        array.add( Data.newData().path( new EntryPath( "myArray" ) ).type( DataTypes.TEXT ).value( "1" ).build() );
        array.set( 1, "2" );

        assertEquals( 2, array.size() );
        assertEquals( new EntryPath( "myArray[0]" ), array.getData( 0 ).getPath() );
        assertEquals( new EntryPath( "myArray[1]" ), array.getData( 1 ).getPath() );
    }

    @Test
    public void add_adding_first_data_with_index_1_throws_exception()
    {
        DataArray array = new DataArray( new EntryPath( "myArray" ), DataTypes.TEXT );
        try
        {
            array.add( Data.newData().path( new EntryPath( "myArray[1]" ) ).type( DataTypes.TEXT ).value( "1" ).build() );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Data [value=1] not added successively to array [myArray] with size 0. Data had unexpected index: 1",
                          e.getMessage() );
        }
    }

    @Test
    public void add_adding_second_data_with_index_2_throws_exception()
    {
        DataArray array = new DataArray( new EntryPath( "myArray" ), DataTypes.TEXT );
        array.add( Data.newData().path( new EntryPath( "myArray" ) ).type( DataTypes.TEXT ).value( "1" ).build() );
        try
        {
            array.add( Data.newData().path( new EntryPath( "myArray[2]" ) ).type( DataTypes.TEXT ).value( "1" ).build() );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Data [value=1] not added successively to array [myArray] with size 1. Data had unexpected index: 2",
                          e.getMessage() );
        }
    }

    @Test
    public void add_adding_data_not_of_expected_type_throws_exception()
    {
        DataArray array = new DataArray( new EntryPath( "myArray" ), DataTypes.DATE );
        try
        {
            array.add( Data.newData().path( new EntryPath( "myArray" ) ).type( DataTypes.TEXT ).value( "1" ).build() );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "DataArray [myArray] expects data of type [Date]. Data [myArray] was of type: Text", e.getMessage() );
        }
    }

    @Test
    public void add_adding_data_with_path_not_within_path_of_array_throws_exception()
    {
        DataArray array = new DataArray( new EntryPath( "mySet[0].myArray" ), DataTypes.TEXT );

        // exercise & verify
        try
        {
            array.add( Data.newData().path( new EntryPath( "mySet[1].myArray[0]" ) ).type( DataTypes.TEXT ).value( "1" ).build() );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Data added to array [mySet[0].myArray] does not have same path: mySet[1].myArray[0]", e.getMessage() );
        }

        // exercise & verify
        try
        {
            array.add( Data.newData().path( new EntryPath( "mySet.myArray[0]" ) ).type( DataTypes.TEXT ).value( "1" ).build() );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Data added to array [mySet[0].myArray] does not have same path: mySet.myArray[0]", e.getMessage() );
        }
    }
}
