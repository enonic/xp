package com.enonic.wem.core.content.data;


import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

import static org.junit.Assert.*;

public class EntriesTest
{
    @Test
    public void setValue_when_given_path_does_not_exists()
    {
        ConfigItems configItems = new ConfigItems();
        FieldSet fieldSet = FieldSet.newBuilder().typeGroup().name( "personalia" ).multiple( true ).build();
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        configItems.addConfigItem( fieldSet );

        Entries entries = new Entries( new EntryPath(), configItems );

        try
        {
            entries.setValue( new EntryPath( "unknown.eyeColour" ), "Brown" );
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
        FieldSet fieldSet = FieldSet.newBuilder().typeGroup().name( "personalia" ).multiple( false ).build();
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );
        ConfigItems configItems = new ConfigItems();
        configItems.addConfigItem( fieldSet );

        Entries entries = new Entries( new EntryPath(), configItems );
        entries.setValue( new EntryPath( "personalia.eyeColour" ), "Brown" );
        entries.setValue( new EntryPath( "personalia.hairColour" ), "Brown" );

        assertEquals( "Brown", entries.getValue( "personalia.eyeColour" ).getValue() );
        assertEquals( "Brown", entries.getValue( "personalia.hairColour" ).getValue() );
    }

    @Test
    public void getValue_when_having_multiple_sub_type_in_single_sub_type()
    {
        FieldSet personalia = FieldSet.newBuilder().typeGroup().name( "personalia" ).label( "Personalia" ).multiple( false ).build();
        FieldSet crimes = FieldSet.newBuilder().typeGroup().name( "crimes" ).multiple( true ).build();
        crimes.addField( Field.newBuilder().name( "description" ).type( FieldTypes.textline ).build() );
        crimes.addField( Field.newBuilder().name( "year" ).type( FieldTypes.textline ).build() );
        personalia.addFieldSet( crimes );
        ConfigItems configItems = new ConfigItems();
        configItems.addConfigItem( personalia );

        Entries entries = new Entries( new EntryPath(), configItems );
        entries.setValue( new EntryPath( "personalia.crimes[0].description" ), "Stole purse from old lady." );
        entries.setValue( new EntryPath( "personalia.crimes[0].year" ), "2011" );
        entries.setValue( new EntryPath( "personalia.crimes[1].description" ), "Drove car in 80 in 50 zone." );
        entries.setValue( new EntryPath( "personalia.crimes[1].year" ), "2012" );

        assertEquals( "Stole purse from old lady.", entries.getValue( "personalia.crimes[0].description" ).getValue() );
        assertEquals( "2011", entries.getValue( "personalia.crimes[0].year" ).getValue() );
        assertEquals( "Drove car in 80 in 50 zone.", entries.getValue( "personalia.crimes[1].description" ).getValue() );
        assertEquals( "2012", entries.getValue( "personalia.crimes[1].year" ).getValue() );
    }

    @Test
    public void getValue_when_having_multiple_sub_type()
    {
        ConfigItems configItems = new ConfigItems();
        FieldSet fieldSet = FieldSet.newBuilder().typeGroup().name( "persons" ).multiple( true ).build();
        fieldSet.addField( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        configItems.addConfigItem( fieldSet );

        Entries entries = new Entries( new EntryPath(), configItems );
        entries.setValue( new EntryPath( "persons[0].name" ), "Arn" );
        entries.setValue( new EntryPath( "persons[0].eyeColour" ), "Brown" );

        assertEquals( "Arn", entries.getValue( "persons[0].name" ).getValue() );
        assertEquals( "Brown", entries.getValue( "persons[0].eyeColour" ).getValue() );
    }
}
