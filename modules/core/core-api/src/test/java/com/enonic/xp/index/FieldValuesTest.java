package com.enonic.xp.index;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldValuesTest
{
    @Test
    void empty()
    {
        assertTrue( FieldValues.empty().isEmpty() );
        assertTrue( FieldValues.empty().getFields().isEmpty() );
        assertTrue( FieldValues.empty().getValues( "anything" ).isEmpty() );
        assertEquals( Optional.empty(), FieldValues.empty().getSingleValue( "anything" ) );
        assertSame( FieldValues.empty(), FieldValues.create().build() );
    }

    @Test
    void values()
    {
        final FieldValues fields =
            FieldValues.create().add( "_name", List.of( "my-node" ) ).add( "data.tags", List.of( "a", "b" ) ).build();

        assertEquals( Set.of( "_name", "data.tags" ), fields.getFields() );
        assertEquals( List.of( "my-node" ), fields.getValues( IndexPath.from( "_name" ) ) );
        assertEquals( Optional.of( "a" ), fields.getSingleValue( "data.tags" ) );
        assertEquals( List.of( "a", "b" ), fields.getValues( "data.tags" ) );
    }

    @Test
    void keys_are_index_path_normalized()
    {
        final FieldValues fields = FieldValues.create().add( "Data.MyField", List.of( 42.0 ) ).build();

        assertEquals( Set.of( "data.myfield" ), fields.getFields() );
        assertEquals( Optional.of( 42.0 ), fields.getSingleValue( "data.myField" ) );
        assertEquals( Optional.of( 42.0 ), fields.getSingleValue( IndexPath.from( "DATA.myfield" ) ) );
    }

    @Test
    void absent_field_is_empty_not_null()
    {
        final FieldValues fields = FieldValues.create().add( "_name", List.of( "a" ) ).build();

        assertTrue( fields.getValues( "no-such-field" ).isEmpty() );
        assertEquals( Optional.empty(), fields.getSingleValue( "no-such-field" ) );
    }
}
