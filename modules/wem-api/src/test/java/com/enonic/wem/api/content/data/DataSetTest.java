package com.enonic.wem.api.content.data;


import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.type.component.ComponentSet;
import com.enonic.wem.api.content.type.component.Components;
import com.enonic.wem.api.content.type.component.inputtype.InputTypes;

import static com.enonic.wem.api.content.type.component.ComponentSet.newComponentSet;
import static com.enonic.wem.api.content.type.component.Input.newInput;
import static org.junit.Assert.*;

public class DataSetTest
{
    @Test
    public void setValue_when_given_path_does_not_exists()
    {
        Components components = new Components();
        ComponentSet componentSet = newComponentSet().name( "personalia" ).multiple( true ).build();
        componentSet.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        components.add( componentSet );

        DataSet dataSet = new DataSet( new EntryPath() );

        try
        {
            dataSet.setData( new EntryPath( "unknown.eyeColour" ), "Brown", DataTypes.TEXT );
        }
        catch ( Exception e )
        {
            Assert.assertTrue( e instanceof IllegalArgumentException );
            Assert.assertTrue( e.getMessage().startsWith( "No Component found at: unknown.eyeColour" ) );
        }
    }

    @Test
    public void getValue_when_having_sub_type()
    {
        ComponentSet componentSet = newComponentSet().name( "personalia" ).multiple( false ).build();
        componentSet.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );
        Components components = new Components();
        components.add( componentSet );

        DataSet dataSet = new DataSet( new EntryPath() );
        dataSet.setData( new EntryPath( "personalia.eyeColour" ), "Brown", DataTypes.TEXT );
        dataSet.setData( new EntryPath( "personalia.hairColour" ), "Brown", DataTypes.TEXT );

        assertEquals( "Brown", dataSet.getData( "personalia.eyeColour" ).getValue() );
        assertEquals( "Brown", dataSet.getData( "personalia.hairColour" ).getValue() );
    }

    @Test
    public void getValue_when_having_multiple_sub_type_in_single_sub_type()
    {
        ComponentSet personalia = newComponentSet().name( "personalia" ).label( "Personalia" ).multiple( true ).build();
        ComponentSet crimes = newComponentSet().name( "crimes" ).multiple( true ).build();
        crimes.add( newInput().name( "description" ).type( InputTypes.TEXT_LINE ).build() );
        crimes.add( newInput().name( "year" ).type( InputTypes.TEXT_LINE ).build() );
        personalia.add( crimes );
        Components components = new Components();
        components.add( personalia );

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
        Components components = new Components();
        ComponentSet componentSet = newComponentSet().name( "persons" ).multiple( true ).build();
        componentSet.add( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        components.add( componentSet );

        DataSet dataSet = new DataSet( new EntryPath() );
        dataSet.setData( new EntryPath( "persons[0].name" ), "Arn", DataTypes.TEXT );
        dataSet.setData( new EntryPath( "persons[0].eyeColour" ), "Brown", DataTypes.TEXT );

        assertEquals( "Arn", dataSet.getData( "persons[0].name" ).getValue() );
        assertEquals( "Brown", dataSet.getData( "persons[0].eyeColour" ).getValue() );
    }

    @Test
    public void array()
    {
        DataSet dataSet = new DataSet( new EntryPath() );
        dataSet.add( Data.newData().path( new EntryPath( "myArray" ) ).value( "1" ).type( DataTypes.TEXT ).build() );
        dataSet.add( Data.newData().path( new EntryPath( "myArray" ) ).value( "2" ).type( DataTypes.TEXT ).build() );
        dataSet.add( Data.newData().path( new EntryPath( "myArray" ) ).value( "3" ).type( DataTypes.TEXT ).build() );

        assertEquals( "1", dataSet.getData( "myArray[0]" ).getValue() );
        assertEquals( "2", dataSet.getData( "myArray[1]" ).getValue() );
        assertEquals( "3", dataSet.getData( "myArray[2]" ).getValue() );
    }


}

