package com.enonic.wem.core.content.data;


import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.SubType;
import com.enonic.wem.core.content.type.configitem.field.type.FieldTypes;

import static org.junit.Assert.*;

public class EntriesTest
{
    @Test
    public void setValue_when_given_path_does_not_exists()
    {
        ConfigItems configItems = new ConfigItems();
        SubType.Builder subTypeBuilder = SubType.newBuilder();
        subTypeBuilder.name( "personalia" );
        subTypeBuilder.label( "Personalia" );
        subTypeBuilder.multiple( true );
        SubType subType = subTypeBuilder.build();
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        configItems.addConfig( subType );

        Entries entries = new Entries( configItems );

        try
        {
            entries.setValue( new ValuePath( "unknown.eyeColour" ), "Brown" );
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
        SubType subType = SubType.newBuilder().name( "personalia" ).multiple( false ).build();
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "hairColour" ).type( FieldTypes.textline ).build() );
        ConfigItems configItems = new ConfigItems();
        configItems.addConfig( subType );

        Entries entries = new Entries( configItems );
        entries.setValue( new ValuePath( "personalia.eyeColour" ), "Brown" );
        entries.setValue( new ValuePath( "personalia.hairColour" ), "Brown" );

        assertEquals( "Brown", entries.getValue( "personalia.eyeColour" ).getValue() );
        assertEquals( "Brown", entries.getValue( "personalia.hairColour" ).getValue() );
    }

    @Test
    public void getValue_when_having_multiple_sub_type_in_single_sub_type()
    {
        SubType personalia = SubType.newBuilder().name( "personalia" ).label( "Personalia" ).multiple( false ).build();
        SubType crimes = SubType.newBuilder().name( "crimes" ).multiple( true ).build();
        crimes.addField( Field.newBuilder().name( "description" ).type( FieldTypes.textline ).build() );
        crimes.addField( Field.newBuilder().name( "year" ).type( FieldTypes.textline ).build() );
        personalia.addSubType( crimes );
        ConfigItems configItems = new ConfigItems();
        configItems.addConfig( personalia );

        Entries entries = new Entries( configItems );
        entries.setValue( new ValuePath( "personalia.crimes[0].description" ), "Stole purse from old lady." );
        entries.setValue( new ValuePath( "personalia.crimes[0].year" ), "2011" );
        entries.setValue( new ValuePath( "personalia.crimes[1].description" ), "Drove car in 80 in 50 zone." );
        entries.setValue( new ValuePath( "personalia.crimes[1].year" ), "2012" );

        assertEquals( "Stole purse from old lady.", entries.getValue( "personalia.crimes[0].description" ).getValue() );
        assertEquals( "2011", entries.getValue( "personalia.crimes[0].year" ).getValue() );
        assertEquals( "Drove car in 80 in 50 zone.", entries.getValue( "personalia.crimes[1].description" ).getValue() );
        assertEquals( "2012", entries.getValue( "personalia.crimes[1].year" ).getValue() );
    }

    @Test
    public void getValue_when_having_multiple_sub_type()
    {
        ConfigItems configItems = new ConfigItems();
        SubType subType = SubType.newBuilder().name( "persons" ).multiple( true ).build();
        subType.addField( Field.newBuilder().name( "name" ).type( FieldTypes.textline ).build() );
        subType.addField( Field.newBuilder().name( "eyeColour" ).type( FieldTypes.textline ).build() );
        configItems.addConfig( subType );

        Entries entries = new Entries( configItems );
        entries.setValue( new ValuePath( "persons[0].name" ), "Arn" );
        entries.setValue( new ValuePath( "persons[0].eyeColour" ), "Brown" );

        assertEquals( "Arn", entries.getValue( "persons[0].name" ).getValue() );
        assertEquals( "Brown", entries.getValue( "persons[0].eyeColour" ).getValue() );
    }
}
