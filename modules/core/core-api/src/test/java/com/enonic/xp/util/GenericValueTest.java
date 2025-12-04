package com.enonic.xp.util;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenericValueTest
{
    @Test
    void stringValue()
    {
        final GenericValue a = GenericValue.stringValue( "a" );
        assertEquals( GenericValue.Type.STRING, a.getType() );
        assertEquals( "a", a.asString() );

        assertThrows( NullPointerException.class, () -> GenericValue.stringValue( null ) );
    }

    @Test
    void booleanValue_true()
    {
        final GenericValue b = GenericValue.booleanValue( true );
        assertEquals( GenericValue.Type.BOOLEAN, b.getType() );
        assertTrue( b.asBoolean() );
    }

    @Test
    void booleanValue_false()
    {
        final GenericValue b = GenericValue.booleanValue( false );
        assertEquals( GenericValue.Type.BOOLEAN, b.getType() );
        assertFalse( b.asBoolean() );
    }

    @Test
    void numberValue_int()
    {
        final GenericValue i = GenericValue.numberValue( 10 );
        assertEquals( GenericValue.Type.NUMBER, i.getType() );
        assertEquals( 10, i.asInteger() );
    }

    @Test
    void numberValue_long()
    {
        final GenericValue i = GenericValue.numberValue( 10L );
        assertEquals( GenericValue.Type.NUMBER, i.getType() );
        assertEquals( 10L, i.asLong() );
    }

    @Test
    void numberValue_maxLong()
    {
        final GenericValue i = GenericValue.numberValue( Long.MAX_VALUE );
        assertEquals( GenericValue.Type.NUMBER, i.getType() );
        assertEquals( Long.MAX_VALUE, i.asLong() );
    }

    @Test
    void numberValue_double()
    {
        final GenericValue i = GenericValue.numberValue( 10.5 );
        assertEquals( GenericValue.Type.NUMBER, i.getType() );
        assertEquals( 10.5, i.asDouble() );
    }

    @Test
    void asString()
    {
        assertEquals( "a", GenericValue.stringValue( "a" ).asString() );
        assertEquals( "10.5", GenericValue.numberValue( 10.5 ).asString() );
        assertEquals( "9223372036854775807", GenericValue.numberValue( Long.MAX_VALUE ).asString() );
        assertEquals( "10", GenericValue.numberValue( 10 ).asString() );
        assertEquals( "true", GenericValue.booleanValue( true ).asString() );
        assertThrows( IllegalStateException.class, () -> GenericValue.list().build().asString() );
        assertThrows( IllegalStateException.class, () -> GenericValue.object().build().asString() );
    }

    @Test
    void list_empty()
    {
        final GenericValue value = GenericValue.list().build();
        assertEquals( GenericValue.Type.LIST, value.getType() );
        assertTrue( value.asList().isEmpty() );
    }

    @Test
    void object_empty()
    {
        final GenericValue value = GenericValue.object().build();
        assertEquals( GenericValue.Type.OBJECT, value.getType() );
        assertTrue( value.properties().isEmpty() );
    }

    @Test
    void asDouble()
    {
        assertEquals( 10.5, GenericValue.numberValue( 10.5 ).asDouble() );
        assertEquals( 10.0, GenericValue.numberValue( 10 ).asDouble() );
        assertEquals( 9.223372036854776E18, GenericValue.numberValue( Long.MAX_VALUE ).asDouble() );
        assertEquals( -9.22E18D, GenericValue.stringValue( "-9.22E18" ).asDouble() );
        assertThrows( NumberFormatException.class, () -> GenericValue.stringValue( "a" ).asDouble() );
        assertThrows( IllegalStateException.class, () -> GenericValue.object().build().asDouble() );
    }

    @Test
    void properties()
    {
        final GenericValue value =
            GenericValue.object().put( "key1", GenericValue.stringValue( "value1" ) ).put( "key2", GenericValue.numberValue( 10 ) ).build();

        assertEquals( GenericValue.Type.OBJECT, value.getType() );

        assertThat( value.properties() ).containsExactly( Map.entry( "key1", GenericValue.stringValue( "value1" ) ),
                                                          Map.entry( "key2", GenericValue.numberValue( 10 ) ) );
        assertThrows( NoSuchElementException.class, () -> value.property( "inexistent" ) );

    }

    @Test
    void properties_incompatible()
    {
        assertTrue( GenericValue.booleanValue( false ).properties().isEmpty() );
    }

    @Test
    void property_incompatible()
    {
        assertThrows( NoSuchElementException.class, () -> GenericValue.booleanValue( false ).property( "any" ) );
    }

    @Test
    void optional_incompatible()
    {
        assertFalse( GenericValue.booleanValue( false ).optional( "any" ).isPresent() );
    }

    @Test
    void asList()
    {
        final GenericValue value = GenericValue.stringList( List.of( "a", "b" ) );

        assertEquals( List.of( GenericValue.stringValue( "a" ), GenericValue.stringValue( "b" ) ), value.asList() );
    }

    @Test
    void asList_wrap_single()
    {
        assertThat( GenericValue.booleanValue( true ).asList() ).containsExactly( GenericValue.booleanValue( true ) );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( GenericValue.class ).verify();
    }
}