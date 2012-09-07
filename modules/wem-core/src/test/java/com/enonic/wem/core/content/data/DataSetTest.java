package com.enonic.wem.core.content.data;


import org.junit.Test;

import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.formitem.Component;
import com.enonic.wem.core.content.type.formitem.FormItemSet;
import com.enonic.wem.core.content.type.formitem.FormItems;
import com.enonic.wem.core.content.type.formitem.fieldtype.FieldTypes;

import static org.junit.Assert.*;

public class DataSetTest
{
    @Test
    public void setValue_when_given_path_does_not_exists()
    {
        FormItems formItems = new FormItems();
        FormItemSet formItemSet = FormItemSet.newBuilder().name( "personalia" ).multiple( true ).build();
        formItemSet.addItem( Component.newBuilder().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() );
        formItems.addFormItem( formItemSet );

        DataSet dataSet = new DataSet( new EntryPath() );

        try
        {
            dataSet.setData( new EntryPath( "unknown.eyeColour" ), "Brown", DataTypes.STRING );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertTrue( e.getMessage().startsWith( "No FormItem found at: unknown.eyeColour" ) );
        }
    }

    @Test
    public void getValue_when_having_sub_type()
    {
        FormItemSet formItemSet = FormItemSet.newBuilder().name( "personalia" ).multiple( false ).build();
        formItemSet.addItem( Component.newBuilder().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() );
        formItemSet.addItem( Component.newBuilder().name( "hairColour" ).type( FieldTypes.TEXT_LINE ).build() );
        FormItems formItems = new FormItems();
        formItems.addFormItem( formItemSet );

        DataSet dataSet = new DataSet( new EntryPath() );
        dataSet.setData( new EntryPath( "personalia.eyeColour" ), "Brown", DataTypes.STRING );
        dataSet.setData( new EntryPath( "personalia.hairColour" ), "Brown", DataTypes.STRING );

        assertEquals( "Brown", dataSet.getData( "personalia.eyeColour" ).getValue() );
        assertEquals( "Brown", dataSet.getData( "personalia.hairColour" ).getValue() );
    }

    @Test
    public void getValue_when_having_multiple_sub_type_in_single_sub_type()
    {
        FormItemSet personalia = FormItemSet.newBuilder().name( "personalia" ).label( "Personalia" ).multiple( true ).build();
        FormItemSet crimes = FormItemSet.newBuilder().name( "crimes" ).multiple( true ).build();
        crimes.addItem( Component.newBuilder().name( "description" ).type( FieldTypes.TEXT_LINE ).build() );
        crimes.addItem( Component.newBuilder().name( "year" ).type( FieldTypes.TEXT_LINE ).build() );
        personalia.addFormItemSet( crimes );
        FormItems formItems = new FormItems();
        formItems.addFormItem( personalia );

        DataSet dataSet = new DataSet( new EntryPath() );
        dataSet.setData( new EntryPath( "personalia.crimes[0].description" ), "Stole purse from old lady.", DataTypes.STRING );
        dataSet.setData( new EntryPath( "personalia.crimes[0].year" ), "2011", DataTypes.STRING );
        dataSet.setData( new EntryPath( "personalia.crimes[1].description" ), "Drove car in 80 in 50 zone.", DataTypes.STRING );
        dataSet.setData( new EntryPath( "personalia.crimes[1].year" ), "2012", DataTypes.STRING );

        assertEquals( "Stole purse from old lady.", dataSet.getData( "personalia.crimes[0].description" ).getValue() );
        assertEquals( "2011", dataSet.getData( "personalia.crimes[0].year" ).getValue() );
        assertEquals( "Drove car in 80 in 50 zone.", dataSet.getData( "personalia.crimes[1].description" ).getValue() );
        assertEquals( "2012", dataSet.getData( "personalia.crimes[1].year" ).getValue() );
    }

    @Test
    public void getValue_when_having_multiple_sub_type()
    {
        FormItems formItems = new FormItems();
        FormItemSet formItemSet = FormItemSet.newBuilder().name( "persons" ).multiple( true ).build();
        formItemSet.addItem( Component.newBuilder().name( "name" ).type( FieldTypes.TEXT_LINE ).build() );
        formItemSet.addItem( Component.newBuilder().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() );
        formItems.addFormItem( formItemSet );

        DataSet dataSet = new DataSet( new EntryPath() );
        dataSet.setData( new EntryPath( "persons[0].name" ), "Arn", DataTypes.STRING );
        dataSet.setData( new EntryPath( "persons[0].eyeColour" ), "Brown", DataTypes.STRING );

        assertEquals( "Arn", dataSet.getData( "persons[0].name" ).getValue() );
        assertEquals( "Brown", dataSet.getData( "persons[0].eyeColour" ).getValue() );
    }


}

