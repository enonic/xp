package com.enonic.wem.api.data;


import java.util.Iterator;

import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.type.ValueTypes;

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
                    set( "myData1", "aaa", ValueTypes.TEXT ).
                    set( "myData2", "bbb", ValueTypes.TEXT ).
                    build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{DataSet.newDataSet().
                    name( "mySet" ).
                    set( "myData1", "aaa", ValueTypes.TEXT ).
                    build(), DataSet.newDataSet().
                    name( "mySet" ).
                    set( "myData1", "aaa", ValueTypes.TEXT ).
                    set( "myData2", "bbb", ValueTypes.TEXT ).
                    set( "myData3", "bbb", ValueTypes.TEXT ).
                    build(), DataSet.newDataSet().
                    name( "mySet" ).
                    set( "myData1", "111", ValueTypes.TEXT ).
                    set( "myData2", "222", ValueTypes.TEXT ).
                    build()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return DataSet.newDataSet().
                    name( "mySet" ).
                    set( "myData1", "aaa", ValueTypes.TEXT ).
                    set( "myData2", "bbb", ValueTypes.TEXT ).
                    build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return DataSet.newDataSet().
                    name( "mySet" ).
                    set( "myData1", "aaa", ValueTypes.TEXT ).
                    set( "myData2", "bbb", ValueTypes.TEXT ).
                    build();
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
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
        dataSet.add( Property.newProperty().name( "myData" ).type( ValueTypes.TEXT ).value( "A value" ).build() );

        assertEquals( "mySet.myData", dataSet.getProperty( "myData" ).getPath().toString() );
        assertEquals( "A value", dataSet.getProperty( "myData" ).getString() );
    }

    @Test
    public void addProperty()
    {
        ContentData contentData = new ContentData();
        Property addedPropertyA = contentData.addProperty( "propA", new Value.Text( "A value" ) );
        Property addedPropertyB = contentData.addProperty( "mySet.propB", new Value.Text( "A second value" ) );

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
        dataSet.add( Property.newProperty().name( "myData" ).type( ValueTypes.TEXT ).value( "1" ).build() );
        dataSet.add( Property.newProperty().name( "myData" ).type( ValueTypes.TEXT ).value( "2" ).build() );

        assertEquals( "1", dataSet.getProperty( "myData" ).getString() );
        assertEquals( "1", dataSet.getProperty( "myData[0]" ).getString() );
        assertEquals( "2", dataSet.getProperty( "myData[1]" ).getString() );
    }

    @Test
    public void add_given_data_of_type_text_when_adding_data_of_other_type_with_same_name_then_exception_is_thrown()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( Property.newProperty().name( "myData" ).type( ValueTypes.TEXT ).value( "A value" ).build() );

        // exercise
        try
        {
            dataSet.add( Property.newProperty().name( "myData" ).type( ValueTypes.HTML_PART ).value( "A value" ).build() );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            // verify
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Array [mySet.myData] expects Property of type [Text]. Property [mySet.myData] was of type: HtmlPart",
                          e.getMessage() );
        }
    }

    @Test
    public void size()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( Property.newProperty().name( "myData" ).type( ValueTypes.TEXT ).value( "1" ).build() );
        dataSet.add( Property.newProperty().name( "myData" ).type( ValueTypes.TEXT ).value( "2" ).build() );

        assertEquals( 2, dataSet.size() );
    }

    @Test
    public void dataCount()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( Property.newProperty().name( "myData" ).type( ValueTypes.TEXT ).value( "1" ).build() );
        dataSet.add( Property.newProperty().name( "myOtherData" ).type( ValueTypes.TEXT ).value( "A" ).build() );
        dataSet.add( Property.newProperty().name( "myData" ).type( ValueTypes.TEXT ).value( "2" ).build() );

        assertEquals( 2, dataSet.nameCount( "myData" ) );
    }

    @Test
    public void dataCount_given_non_existing_data_then_0_is_returned()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( Property.newProperty().name( "myData" ).type( ValueTypes.TEXT ).value( "2" ).build() );

        assertEquals( 0, dataSet.nameCount( "nonExistingData" ) );
    }

    @Test
    public void add_given_two_data_added_with_same_path_then_array_is_created()
    {
        Property myArray1 = Property.newProperty().name( "myArray" ).type( ValueTypes.TEXT ).value( "1" ).build();
        Property myArray2 = Property.newProperty().name( "myArray" ).type( ValueTypes.TEXT ).value( "2" ).build();

        DataSet dataSet = new ContentData();
        dataSet.add( myArray1 );
        dataSet.add( myArray2 );

        assertEquals( "1", dataSet.getProperty( "myArray[0]" ).getString() );
        assertEquals( "2", dataSet.getProperty( "myArray[1]" ).getString() );
    }

    @Test
    public void dataNames()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( Property.newProperty().name( "myData" ).type( ValueTypes.TEXT ).value( "1" ).build() );
        dataSet.add( Property.newProperty().name( "myOtherData" ).type( ValueTypes.TEXT ).value( "A" ).build() );
        dataSet.add( Property.newProperty().name( "myData" ).type( ValueTypes.TEXT ).value( "2" ).build() );

        Iterator<String> dataNames = dataSet.dataNames().iterator();
        assertEquals( "myData", dataNames.next() );
        assertEquals( "myOtherData", dataNames.next() );
        assertEquals( false, dataNames.hasNext() );
    }

    @Test
    public void getData()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "myData" ), new Value.Text( "something" ) );

        assertNotNull( dataSet.getData( "myData" ) );
    }

    @Test
    public void getDataSet_given_path_to_non_existing_DataSet_then_null_is_returned()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "mySet.myData" ), new Value.Text( "something" ) );

        assertNull( dataSet.getDataSet( "notExisting" ) );
        assertNull( dataSet.getDataSet( "notExisting", 0 ) );
    }

    @Test
    public void getData_given_path_to_non_existing_data_then_null_is_returned()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "myData" ), new Value.Text( "something" ) );

        assertNull( dataSet.getProperty( "notExisting" ) );
        assertNull( dataSet.getProperty( "notExisting", 0 ) );
    }

    @Test
    public void getData_given_array_when_getting_with_index_then_expected_data_is_returned()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "myArray[0]" ), new Value.Text( "2a" ) );
        dataSet.setProperty( DataPath.from( "myArray[1]" ), new Value.Text( "2b" ) );

        assertEquals( "2a", dataSet.getProperty( "myArray", 0 ).getString() );
        assertEquals( "2b", dataSet.getProperty( "myArray", 1 ).getString() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getData_given_name_with_index_then_exception_is_thrown()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "myData" ), new Value.Text( "1" ) );

        // exercise
        dataSet.getProperty( "myData[0]", 1 );
    }

    @Test
    public void getValue()
    {
        DataSet dataSet = new ContentData();
        dataSet.add( Property.newProperty().name( "myData" ).type( ValueTypes.TEXT ).value( "1" ).build() );

        assertEquals( "1", dataSet.getValue( "myData" ).getObject() );
        assertEquals( "1", dataSet.getValue( DataPath.from( "myData" ) ).getObject() );
    }

    @Test
    public void getValue_when_having_array_of_set_within_single_set()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "personalia.crimes[0].description" ), new Value.Text( "Stole purse from old lady." ) );
        dataSet.setProperty( DataPath.from( "personalia.crimes[0].year" ), new Value.Text( "2011" ) );
        dataSet.setProperty( DataPath.from( "personalia.crimes[1].description" ), new Value.Text( "Drove car in 80 in 50 zone." ) );
        dataSet.setProperty( DataPath.from( "personalia.crimes[1].year" ), new Value.Text( "2012" ) );

        assertEquals( "Stole purse from old lady.", dataSet.getProperty( "personalia.crimes[0].description" ).getObject() );
        assertEquals( "2011", dataSet.getProperty( "personalia.crimes[0].year" ).getObject() );
        assertEquals( "Drove car in 80 in 50 zone.", dataSet.getProperty( "personalia.crimes[1].description" ).getObject() );
        assertEquals( "2012", dataSet.getProperty( "personalia.crimes[1].year" ).getObject() );
    }

    @Test
    public void getValue_when_having_multiple_mixin()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "persons[0].name" ), new Value.Text( "Arn" ) );
        dataSet.setProperty( DataPath.from( "persons[0].eyeColour" ), new Value.Text( "Brown" ) );

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
        dataSet.setProperty( DataPath.from( "myData" ), new Value.Text( "1" ) );

        assertEquals( "1", dataSet.getProperty( "myData" ).getString() );
        assertEquals( "1", dataSet.getProperty( "myData" ).getValue( 0 ).asString() );
        assertEquals( "1", dataSet.getValue( "myData" ).asString() );
    }

    @Test
    public void setProperty_root_set_with_two_entries()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "myData1" ), new Value.Text( "1" ) );
        dataSet.setProperty( DataPath.from( "myData2" ), new Value.Text( "2" ) );

        assertEquals( "1", dataSet.getProperty( "myData1" ).getString() );
        assertEquals( "2", dataSet.getProperty( "myData2" ).getString() );
    }

    @Test
    public void setProperty_subSet_with_two_entries()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "set.myData1" ), new Value.Text( "1" ) );
        dataSet.setProperty( DataPath.from( "set.myData2" ), new Value.Text( "2" ) );

        assertEquals( "1", dataSet.getProperty( "set.myData1" ).getString() );
        assertEquals( "2", dataSet.getValue( "set.myData2" ).asString() );
    }

    @Test
    public void setProperty_given_one_data_added_and_a_second_data_with_same_name_set_at_index_one_then_array_is_created()
    {
        DataSet dataSet = new ContentData();
        dataSet.add( Property.newProperty().name( "myArray" ).type( ValueTypes.TEXT ).value( "1" ).build() );
        dataSet.setProperty( DataPath.from( "myArray[1]" ), new Value.Text( "2" ) );

        assertEquals( "1", dataSet.getValue( "myArray[0]" ).getObject() );
        assertEquals( "2", dataSet.getValue( "myArray[1]" ).getObject() );
    }

    @Test
    public void setProperty_given_array_index_set_twice_then_value_of_last_is_returned()
    {
        DataSet dataSet = new ContentData();
        dataSet.add( Property.newProperty().name( "myArray" ).type( ValueTypes.TEXT ).value( "1" ).build() );

        // exercise
        dataSet.setProperty( DataPath.from( "myArray[1]" ), new Value.Text( "2a" ) );
        dataSet.setProperty( DataPath.from( "myArray[1]" ), new Value.Text( "2b" ) );

        // verify
        assertEquals( "2b", dataSet.getProperty( "myArray", 1 ).getString() );
    }

    @Test
    public void setProperty_xx()
    {
        DataSet dataSet = new ContentData();

        // exercise
        dataSet.setProperty( "myData", new Value.WholeNumber( 123 ) );

        // verify
        assertEquals( new Long( 123 ), dataSet.getProperty( "myData" ).getLong() );
    }

    @Test
    public void setProperty_given_unsuccessive_index_then_IllegalArgumentException_is_thrown()
    {
        DataSet rootSet = new ContentData();
        rootSet.setProperty( "myText[0]", new Value.Text( "My value 1" ) );
        try
        {
            rootSet.setProperty( "myText[2]", new Value.Text( "My value 2" ) );
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
        Value.WholeNumber value1 = new Value.WholeNumber( 0 );
        Value.WholeNumber value2 = new Value.WholeNumber( 0 );
        dataSet.setProperty( "mySet.prop", value1 );
        dataSet.setProperty( "mySet.prop[1]", value2 );

        assertEquals( 0, dataSet.getProperty( "mySet.prop[0]" ).getArrayIndex() );
        assertEquals( 1, dataSet.getProperty( "mySet.prop[1]" ).getArrayIndex() );
    }

    @Test
    public void iterator_data_is_returned_in_inserted_order()
    {
        DataSet dataSet = new ContentData();
        dataSet.setProperty( DataPath.from( "myData1" ), new Value.Text( "1" ) );
        dataSet.setProperty( DataPath.from( "myArray[0]" ), new Value.Text( "a" ) );
        dataSet.setProperty( DataPath.from( "myData2" ), new Value.Text( "2" ) );
        dataSet.setProperty( DataPath.from( "myArray[1]" ), new Value.Text( "b" ) );

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
        dataSet.setProperty( DataPath.from( "myData" ), new Value.Text( "1" ) );
        dataSet.setProperty( DataPath.from( "myOtherData" ), new Value.Text( "2" ) );

        assertEquals( "{ myData, myOtherData }", dataSet.toString() );
    }

    @Test
    public void tostring_given_array()
    {
        DataSet rootSet = new ContentData();
        rootSet.add( DataSet.newDataSet().name( "mySet" ).build() );

        DataSet mySet = DataSet.newDataSet().name( "mySet" ).build();
        mySet.setProperty( DataPath.from( "myData[0]" ), new Value.Text( "1" ) );
        mySet.setProperty( DataPath.from( "myData[1]" ), new Value.Text( "2" ) );
        rootSet.add( mySet );

        assertEquals( "mySet[1] { myData, myData[1] }", mySet.toString() );
    }

    @Test
    public void toRootDataSet()
    {
        DataSet dataSet = DataSet.newDataSet().name( "mySet" ).build();
        dataSet.add( Property.newProperty().name( "myData" ).type( ValueTypes.TEXT ).value( "A value" ).build() );
        RootDataSet rootDataSet = dataSet.toRootDataSet();

        assertEquals( true, rootDataSet.isRoot() );
        assertEquals( "", rootDataSet.getName() );
    }

}

