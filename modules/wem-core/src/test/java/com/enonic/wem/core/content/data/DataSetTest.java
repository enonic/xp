package com.enonic.wem.core.content.data;


import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

import static org.junit.Assert.*;

public class DataSetTest
{
    @Test
    public void setValue_when_given_path_does_not_exists()
    {
        ConfigItems configItems = new ConfigItems();
        FieldSet fieldSet = FieldSet.newBuilder().name( "personalia" ).multiple( true ).build();
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        configItems.addConfigItem( fieldSet );

        DataSet dataSet = new DataSet( new EntryPath(), configItems );

        try
        {
            dataSet.setData( new EntryPath( "unknown.eyeColour" ), "Brown" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertTrue( e.getMessage().startsWith( "No ConfigItem found at: unknown.eyeColour" ) );
        }
    }

    @Test
    public void getValue_when_having_sub_type()
    {
        FieldSet fieldSet = FieldSet.newBuilder().name( "personalia" ).multiple( false ).build();
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );
        ConfigItems configItems = new ConfigItems();
        configItems.addConfigItem( fieldSet );

        DataSet dataSet = new DataSet( new EntryPath(), configItems );
        dataSet.setData( new EntryPath( "personalia.eyeColour" ), "Brown" );
        dataSet.setData( new EntryPath( "personalia.hairColour" ), "Brown" );

        assertEquals( "Brown", dataSet.getData( "personalia.eyeColour" ).getValue() );
        assertEquals( "Brown", dataSet.getData( "personalia.hairColour" ).getValue() );
    }

    @Test
    public void getValue_when_having_multiple_sub_type_in_single_sub_type()
    {
        FieldSet personalia = FieldSet.newBuilder().name( "personalia" ).label( "Personalia" ).multiple( true ).build();
        FieldSet crimes = FieldSet.newBuilder().name( "crimes" ).multiple( true ).build();
        crimes.addField( Field.newBuilder().name( "description" ).type( FieldTypes.textline ).build() );
        crimes.addField( Field.newBuilder().name( "year" ).type( FieldTypes.textline ).build() );
        personalia.addFieldSet( crimes );
        ConfigItems configItems = new ConfigItems();
        configItems.addConfigItem( personalia );

        DataSet dataSet = new DataSet( new EntryPath(), configItems );
        dataSet.setData( new EntryPath( "personalia.crimes[0].description" ), "Stole purse from old lady." );
        dataSet.setData( new EntryPath( "personalia.crimes[0].year" ), "2011" );
        dataSet.setData( new EntryPath( "personalia.crimes[1].description" ), "Drove car in 80 in 50 zone." );
        dataSet.setData( new EntryPath( "personalia.crimes[1].year" ), "2012" );

        assertEquals( "Stole purse from old lady.", dataSet.getData( "personalia.crimes[0].description" ).getValue() );
        assertEquals( "2011", dataSet.getData( "personalia.crimes[0].year" ).getValue() );
        assertEquals( "Drove car in 80 in 50 zone.", dataSet.getData( "personalia.crimes[1].description" ).getValue() );
        assertEquals( "2012", dataSet.getData( "personalia.crimes[1].year" ).getValue() );
    }

    @Test
    public void getValue_when_having_multiple_sub_type()
    {
        ConfigItems configItems = new ConfigItems();
        FieldSet fieldSet = FieldSet.newBuilder().name( "persons" ).multiple( true ).build();
        fieldSet.addField( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        configItems.addConfigItem( fieldSet );

        DataSet dataSet = new DataSet( new EntryPath(), configItems );
        dataSet.setData( new EntryPath( "persons[0].name" ), "Arn" );
        dataSet.setData( new EntryPath( "persons[0].eyeColour" ), "Brown" );

        assertEquals( "Arn", dataSet.getData( "persons[0].name" ).getValue() );
        assertEquals( "Brown", dataSet.getData( "persons[0].eyeColour" ).getValue() );
    }
}
