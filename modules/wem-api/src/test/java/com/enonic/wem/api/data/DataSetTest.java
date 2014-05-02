package com.enonic.wem.api.data;


import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.support.AbstractEqualsTest;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

public class DataSetTest
{

    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return DataSet.newDataSet().
                    name( "mySet" ).
                    set( "myData1", "aaa", ValueTypes.STRING ).
                    set( "myData2", "bbb", ValueTypes.STRING ).
                    build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{DataSet.newDataSet().
                    name( "mySet" ).
                    set( "myData1", "aaa", ValueTypes.STRING ).
                    build(), DataSet.newDataSet().
                    name( "mySet" ).
                    set( "myData1", "aaa", ValueTypes.STRING ).
                    set( "myData2", "bbb", ValueTypes.STRING ).
                    set( "myData3", "bbb", ValueTypes.STRING ).
                    build(), DataSet.newDataSet().
                    name( "mySet" ).
                    set( "myData1", "111", ValueTypes.STRING ).
                    set( "myData2", "222", ValueTypes.STRING ).
                    build()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return DataSet.newDataSet().
                    name( "mySet" ).
                    set( "myData1", "aaa", ValueTypes.STRING ).
                    set( "myData2", "bbb", ValueTypes.STRING ).
                    build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return DataSet.newDataSet().
                    name( "mySet" ).
                    set( "myData1", "aaa", ValueTypes.STRING ).
                    set( "myData2", "bbb", ValueTypes.STRING ).
                    build();
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void valueEquals_given_DataSet_with_equal_Property_then_true_is_returned()
    {
        DataSet a = new DataSet( "a" );
        a.setProperty( "p1", Value.newString( "v1" ) );
        DataSet b = new DataSet( "b" );
        b.setProperty( "p1", Value.newString( "v1" ) );

        assertTrue( a.valueEquals( b ) );
    }

    @Test
    public void valueEquals_given_DataSet_with_unequal_Property_then_false_is_returned()
    {
        DataSet a = new DataSet( "a" );
        a.setProperty( "p1", Value.newString( "v1" ) );
        DataSet b = new DataSet( "b" );
        b.setProperty( "p1", Value.newString( "v2" ) );

        assertFalse( a.valueEquals( b ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void valueEquals_given_a_Property_then_exception_is_thrown()
    {
        DataSet a = new DataSet( "a" );
        a.setProperty( "p1", Value.newString( "v1" ) );

        assertTrue( a.valueEquals( new Property.String( "b", "1" ) ) );
    }

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
    public void add_Property()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( new Property.String( "myData", "A value" ) );

        assertEquals( "mySet.myData", dataSet.getProperty( "myData" ).getPath().toString() );
        assertEquals( "A value", dataSet.getProperty( "myData" ).getString() );
    }

    @Test
    public void addProperty()
    {
        ContentData contentData = new ContentData();
        Property addedPropertyA = contentData.addProperty( "propA", Value.newString( "A value" ) );
        Property addedPropertyB = contentData.addProperty( "mySet.propB", Value.newString( "A second value" ) );

        assertSame( addedPropertyA, contentData.getProperty( "propA" ) );
        assertEquals( "propA", contentData.getProperty( "propA" ).getPath().toString() );
        assertEquals( "A value", contentData.getProperty( "propA" ).getString() );

        assertSame( addedPropertyB, contentData.getProperty( "mySet.propB" ) );
        assertEquals( "mySet.propB", contentData.getProperty( "mySet.propB" ).getPath().toString() );
        assertEquals( "A second value", contentData.getProperty( "mySet.propB" ).getString() );

    }

    @Test
    public void add_more()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( new Property.String( "myData", "1" ) );
        dataSet.add( new Property.String( "myData", "2" ) );

        assertEquals( "1", dataSet.getProperty( "myData" ).getString() );
        assertEquals( "1", dataSet.getProperty( "myData[0]" ).getString() );
        assertEquals( "2", dataSet.getProperty( "myData[1]" ).getString() );
    }

    @Test
    public void add_given_data_of_type_text_when_adding_data_of_other_type_with_same_name_then_exception_is_thrown()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( new Property.String( "myData", "A value" ) );

        // exercise
        try
        {
            dataSet.add( new Property.HtmlPart( "myData", "A value" ) );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            // verify
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Array [mySet.myData] expects Property of type [String]. Property [mySet.myData] was of type: HtmlPart",
                          e.getMessage() );
        }
    }

    @Test
    public void size()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( new Property.String( "myData", "1" ) );
        dataSet.add( new Property.String( "myData", "2" ) );

        assertEquals( 2, dataSet.size() );
    }

    @Test
    public void dataCount()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( new Property.String( "myData", "1" ) );
        dataSet.add( new Property.String( "myOtherData", "A" ) );
        dataSet.add( new Property.String( "myData", "2" ) );

        assertEquals( 2, dataSet.nameCount( "myData" ) );
    }

    @Test
    public void dataCount_given_non_existing_data_then_0_is_returned()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( new Property.String( "myData", "2" ) );

        assertEquals( 0, dataSet.nameCount( "nonExistingData" ) );
    }

    @Test
    public void add_given_two_data_added_with_same_path_then_array_is_created()
    {
        Property myArray1 = new Property.String( "myArray", "1" );
        Property myArray2 = new Property.String( "myArray", "2" );

        DataSet dataSet = new ContentData();
        dataSet.add( myArray1 );
        dataSet.add( myArray2 );

        assertEquals( "1", dataSet.getProperty( "myArray[0]" ).getString() );
        assertEquals( "2", dataSet.getProperty( "myArray[1]" ).getString() );
    }

    @Test
    public void base_path_is_same_for_same_path()
    {
        Property myArray1 = new Property.String( "myArray", "1" );
        Property myArray2 = new Property.String( "myArray", "2" );

        DataSet dataSet = new ContentData();
        dataSet.add( myArray1 );
        dataSet.add( myArray2 );

        assertTrue( dataSet.getProperty( "myArray[0]" ).getBasePath().equals( dataSet.getProperty( "myArray[1]" ).getBasePath() ) );
        assertFalse( dataSet.getProperty( "myArray[0]" ).getPath().equals( dataSet.getProperty( "myArray[1]" ).getPath() ) );
        assertEquals( "myArray", dataSet.getProperty( "myArray[0]" ).getBasePath().toString() );
    }

    @Test
    public void dataNames()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( new Property.String( "myData", "1" ) );
        dataSet.add( new Property.String( "myOtherData", "A" ) );
        dataSet.add( new Property.String( "myData", "2" ) );

        Iterator<String> dataNames = dataSet.dataNames().iterator();
        assertEquals( "myOtherData", dataNames.next() );
        assertEquals( "myData", dataNames.next() );
        assertEquals( false, dataNames.hasNext() );
    }

    @Test
    public void getData()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "myData" ), Value.newString( "something" ) );

        assertNotNull( dataSet.getData( "myData" ) );
    }

    @Test
    public void getDataSet_given_path_to_non_existing_DataSet_then_null_is_returned()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "mySet.myData" ), Value.newString( "something" ) );

        assertNull( dataSet.getDataSet( "notExisting" ) );
        assertNull( dataSet.getDataSet( "notExisting", 0 ) );
    }

    @Test
    public void getData_given_path_to_non_existing_data_then_null_is_returned()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "myData" ), Value.newString( "something" ) );

        assertNull( dataSet.getProperty( "notExisting" ) );
        assertNull( dataSet.getProperty( "notExisting", 0 ) );
    }

    @Test
    public void getData_given_array_when_getting_with_index_then_expected_data_is_returned()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "myArray[0]" ), Value.newString( "2a" ) );
        dataSet.setProperty( DataPath.from( "myArray[1]" ), Value.newString( "2b" ) );

        assertEquals( "2a", dataSet.getProperty( "myArray", 0 ).getString() );
        assertEquals( "2b", dataSet.getProperty( "myArray", 1 ).getString() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getData_given_name_with_index_then_exception_is_thrown()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "myData" ), Value.newString( "1" ) );

        // exercise
        dataSet.getProperty( "myData[0]", 1 );
    }

    @Test
    public void getValue()
    {
        DataSet dataSet = new ContentData();
        dataSet.add( new Property.String( "myData", "1" ) );

        assertEquals( "1", dataSet.getValue( "myData" ).getObject() );
        assertEquals( "1", dataSet.getValue( DataPath.from( "myData" ) ).getObject() );
    }

    @Test
    public void getValue_when_having_array_of_set_within_single_set()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "personalia.crimes[0].description" ), Value.newString( "Stole purse from old lady." ) );
        dataSet.setProperty( DataPath.from( "personalia.crimes[0].year" ), Value.newString( "2011" ) );
        dataSet.setProperty( DataPath.from( "personalia.crimes[1].description" ), Value.newString( "Drove car in 80 in 50 zone." ) );
        dataSet.setProperty( DataPath.from( "personalia.crimes[1].year" ), Value.newString( "2012" ) );

        assertEquals( "Stole purse from old lady.", dataSet.getProperty( "personalia.crimes[0].description" ).getObject() );
        assertEquals( "2011", dataSet.getProperty( "personalia.crimes[0].year" ).getObject() );
        assertEquals( "Drove car in 80 in 50 zone.", dataSet.getProperty( "personalia.crimes[1].description" ).getObject() );
        assertEquals( "2012", dataSet.getProperty( "personalia.crimes[1].year" ).getObject() );
    }

    @Test
    public void getValue_when_having_multiple_mixin()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "persons[0].name" ), Value.newString( "Arn" ) );
        dataSet.setProperty( DataPath.from( "persons[0].eyeColour" ), Value.newString( "Brown" ) );

        assertEquals( "Arn", dataSet.getProperty( "persons[0].name" ).getObject() );
        assertEquals( "Brown", dataSet.getProperty( "persons[0].eyeColour" ).getObject() );
    }

    @Test
    public void getDataSet()
    {
        DataSet contentData = new ContentData();
        contentData.add( DataSet.newDataSet().name( "mySet" ).build() );
        contentData.add( DataSet.newDataSet().name( "myOtherSet" ).build() );

        assertEquals( "mySet", contentData.getDataSet( "mySet" ).getPath().toString() );
        assertEquals( "mySet", contentData.getDataSet( "mySet", 0 ).getPath().toString() );
        assertEquals( "mySet", contentData.getDataSet( DataPath.from( "mySet" ) ).getPath().toString() );
    }

    @Test
    public void setProperty_root_set_with_one_data()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "myData" ), Value.newString( "1" ) );

        assertEquals( "1", dataSet.getProperty( "myData" ).getString() );
        assertEquals( "1", dataSet.getProperty( "myData" ).getValue( 0 ).asString() );
        assertEquals( "1", dataSet.getValue( "myData" ).asString() );
    }

    @Test
    public void setProperty_root_set_with_two_entries()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "myData1" ), Value.newString( "1" ) );
        dataSet.setProperty( DataPath.from( "myData2" ), Value.newString( "2" ) );

        assertEquals( "1", dataSet.getProperty( "myData1" ).getString() );
        assertEquals( "2", dataSet.getProperty( "myData2" ).getString() );
    }

    @Test
    public void setProperty_subSet_with_two_entries()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "set.myData1" ), Value.newString( "1" ) );
        dataSet.setProperty( DataPath.from( "set.myData2" ), Value.newString( "2" ) );

        assertEquals( "1", dataSet.getProperty( "set.myData1" ).getString() );
        assertEquals( "2", dataSet.getValue( "set.myData2" ).asString() );
    }

    @Test
    public void setProperty_given_one_data_added_and_a_second_data_with_same_name_set_at_index_one_then_array_is_created()
    {
        DataSet dataSet = new ContentData();
        dataSet.add( new Property.String( "myArray", "1" ) );
        dataSet.setProperty( DataPath.from( "myArray[1]" ), Value.newString( "2" ) );

        assertEquals( "1", dataSet.getValue( "myArray[0]" ).getObject() );
        assertEquals( "2", dataSet.getValue( "myArray[1]" ).getObject() );
    }

    @Test
    public void setProperty_given_array_index_set_twice_then_value_of_last_is_returned()
    {
        DataSet dataSet = new ContentData();
        dataSet.add( new Property.String( "myArray", "1" ) );

        // exercise
        dataSet.setProperty( DataPath.from( "myArray[1]" ), Value.newString( "2a" ) );
        dataSet.setProperty( DataPath.from( "myArray[1]" ), Value.newString( "2b" ) );

        // verify
        assertEquals( "2b", dataSet.getProperty( "myArray", 1 ).getString() );
    }

    @Test
    public void setProperty_xx()
    {
        DataSet dataSet = new ContentData();

        // exercise
        dataSet.setProperty( "myData", Value.newLong( 123 ) );

        // verify
        assertEquals( new Long( 123 ), dataSet.getProperty( "myData" ).getLong() );
    }

    @Test
    public void setProperty_given_unsuccessive_index_then_IllegalArgumentException_is_thrown()
    {
        DataSet rootSet = new ContentData();
        rootSet.setProperty( "myText[0]", Value.newString( "My value 1" ) );
        try
        {
            rootSet.setProperty( "myText[2]", Value.newString( "My value 2" ) );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Property [myText[2]] expected to be given a successive index [1]: 2", e.getMessage() );
        }
    }

    @Test
    public void getProperty_given_equal_values_at_successive_indexes_then_property_has_successive_indexes()
    {
        DataSet dataSet = new ContentData();
        Value<Long> value1 = Value.newLong( 0 );
        Value<Long> value2 = Value.newLong( 0 );
        dataSet.setProperty( "mySet.prop", value1 );
        dataSet.setProperty( "mySet.prop[1]", value2 );

        assertEquals( 0, dataSet.getProperty( "mySet.prop[0]" ).getArrayIndex() );
        assertEquals( 1, dataSet.getProperty( "mySet.prop[1]" ).getArrayIndex() );
    }

    @Test
    public void iterator_data_is_returned_in_inserted_order()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "myData1" ), Value.newString( "1" ) );
        dataSet.setProperty( DataPath.from( "myArray[0]" ), Value.newString( "a" ) );
        dataSet.setProperty( DataPath.from( "myData2" ), Value.newString( "2" ) );
        dataSet.setProperty( DataPath.from( "myArray[1]" ), Value.newString( "b" ) );

        Iterator<Data> it = dataSet.iterator();
        assertEquals( DataId.from( "myData1", 0 ), it.next().getDataId() );
        assertEquals( DataId.from( "myArray", 0 ), it.next().getDataId() );
        assertEquals( DataId.from( "myData2", 0 ), it.next().getDataId() );
        assertEquals( DataId.from( "myArray", 1 ), it.next().getDataId() );
    }

    @Test
    public void tostring_given_two_data()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "myData" ), Value.newString( "1" ) );
        dataSet.setProperty( DataPath.from( "myOtherData" ), Value.newString( "2" ) );

        assertEquals( "{ myData, myOtherData }", dataSet.toString() );
    }

    @Test
    public void tostring_given_array()
    {
        DataSet rootSet = new ContentData();
        rootSet.add( DataSet.newDataSet().name( "mySet" ).build() );

        DataSet mySet = DataSet.newDataSet().name( "mySet" ).build();
        mySet.setProperty( DataPath.from( "myData[0]" ), Value.newString( "1" ) );
        mySet.setProperty( DataPath.from( "myData[1]" ), Value.newString( "2" ) );
        rootSet.add( mySet );

        assertEquals( "mySet[1] { myData, myData[1] }", mySet.toString() );
    }

    @Test
    public void toRootDataSet()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( new Property.String( "myData", "A value" ) );
        RootDataSet rootDataSet = dataSet.toRootDataSet();

        assertEquals( true, rootDataSet.isRoot() );
        assertEquals( "", rootDataSet.getName() );
    }

    @Test
    public void getDataSets()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.setProperty( "prop", Value.newString( "1" ) );

        DataSet set1 = DataSet.newDataSet().name( "mySet" ).build();
        set1.setProperty( "prop", Value.newString( "1" ) );
        dataSet.add( set1 );

        DataSet set2 = DataSet.newDataSet().name( "mySet" ).build();
        set2.setProperty( "prop", Value.newString( "1" ) );
        dataSet.add( set2 );

        // exercise
        List<DataSet> foundDataSets = dataSet.getDataSets();

        // verify
        assertEquals( 2, foundDataSets.size() );
    }

}

