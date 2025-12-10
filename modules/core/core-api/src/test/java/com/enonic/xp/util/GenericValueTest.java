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
        assertThrows( IllegalStateException.class, () -> GenericValue.newList().build().asString() );
        assertThrows( IllegalStateException.class, () -> GenericValue.newObject().build().asString() );
    }

    @Test
    void asDouble()
    {
        assertEquals( 10.5, GenericValue.numberValue( 10.5 ).asDouble() );
        assertEquals( 10.0, GenericValue.numberValue( 10 ).asDouble() );
        assertEquals( 9.223372036854776E18, GenericValue.numberValue( Long.MAX_VALUE ).asDouble() );
        assertEquals( -9.22E18D, GenericValue.stringValue( "-9.22E18" ).asDouble() );
        assertThrows( NumberFormatException.class, () -> GenericValue.stringValue( "a" ).asDouble() );
        assertThrows( IllegalStateException.class, () -> GenericValue.newObject().build().asDouble() );
    }

    @Test
    void asLong()
    {
        assertEquals( 10L, GenericValue.numberValue( 10 ).asLong() );
        assertEquals( Long.MAX_VALUE, GenericValue.numberValue( Long.MAX_VALUE ).asLong() );
        assertEquals( 9007199254740993L, GenericValue.stringValue( "9007199254740993" ).asLong() );
        assertThrows( NumberFormatException.class, () -> GenericValue.stringValue( "a" ).asLong() );
        assertThrows( ArithmeticException.class, () -> GenericValue.numberValue( Double.MAX_VALUE ).asLong() );
        assertThrows( IllegalStateException.class, () -> GenericValue.newList().build().asLong() );
    }

    @Test
    void asInteger()
    {
        assertEquals( 10, GenericValue.numberValue( 10 ).asInteger() );
        assertEquals( Integer.MAX_VALUE, GenericValue.numberValue( Integer.MAX_VALUE ).asInteger() );
        assertEquals( -2147483648, GenericValue.stringValue( "-2147483648" ).asInteger() );
        assertThrows( NumberFormatException.class, () -> GenericValue.stringValue( "a" ).asInteger() );
        assertThrows( ArithmeticException.class, () -> GenericValue.numberValue( Integer.MAX_VALUE + 1D ).asInteger() );
        assertThrows( ArithmeticException.class, () -> GenericValue.numberValue( Long.MAX_VALUE ).asInteger() );
        assertThrows( IllegalStateException.class, () -> GenericValue.newObject().build().asInteger() );
    }

    @Test
    void asBoolean()
    {
        assertFalse( GenericValue.booleanValue( false ).asBoolean() );
        assertTrue( GenericValue.booleanValue( true ).asBoolean() );
        assertThrows( IllegalStateException.class, () -> GenericValue.stringValue( "true" ).asBoolean() );
    }

    @Test
    void properties()
    {
        final GenericValue value = GenericValue.newObject()
            .put( "key1", GenericValue.stringValue( "value1" ) )
            .put( "key2", GenericValue.numberValue( 10 ) )
            .build();

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
    void toStringList()
    {
        final GenericValue list = GenericValue.newList()
            .add( GenericValue.stringValue( "a" ) )
            .add( GenericValue.stringValue( "b" ) )
            .add( GenericValue.stringValue( "c" ) )
            .build();
        assertThat( list.toStringList() ).containsExactly( "a", "b", "c" );
    }

    @Test
    void toStringList_single()
    {
        final GenericValue single = GenericValue.stringValue( "single" );

        assertThat( single.toStringList() ).containsExactly( "single" );
    }

    @Test
    void toStringList_converts()
    {
        final GenericValue list = GenericValue.newList()
            .add( GenericValue.numberValue( 1 ) )
            .add( GenericValue.numberValue( 2 ) )
            .add( GenericValue.numberValue( 3 ) )
            .build();

        assertThat( list.toStringList() ).containsExactly( "1", "2", "3" );
    }

    @Test
    void toRawJava()
    {
        final GenericValue obj = GenericValue.newObject()
            .put( "name", "test" )
            .put( "bigNumber", Long.MAX_VALUE )
            .put( "integer", 10 )
            .put( "double", Double.MAX_VALUE )
            .put( "boolean", true )
            .put( "list", GenericValue.fromRawJava( List.of( "a", "b" ) ) )
            .build();

        assertThat( obj.toRawJava() ).isEqualTo(
            Map.of( "name", "test", "bigNumber", Long.MAX_VALUE, "integer", 10, "double", Double.MAX_VALUE, "boolean", true, "list",
                    List.of( "a", "b" ) ) );
    }

    @Test
    void toRawJava_single()
    {
        assertThat( GenericValue.stringValue( "a" ).toRawJava() ).isEqualTo( "a" );
        assertThat( GenericValue.numberValue( Double.MAX_VALUE ).toRawJava() ).isEqualTo( Double.MAX_VALUE );
        assertThat( GenericValue.numberValue( Long.MAX_VALUE ).toRawJava() ).isEqualTo( Long.MAX_VALUE );
    }

    @Test
    void toRawJs()
    {
        final GenericValue obj = GenericValue.newObject()
            .put( "name", "test" )
            .put( "bigNumber", Long.MAX_VALUE )
            .put( "integer", 10 )
            .put( "double", Double.MAX_VALUE )
            .put( "boolean", true )
            .put( "list", GenericValue.fromRawJava( List.of( "a", "b" ) ) )
            .build();

        assertThat( obj.toRawJs() ).isEqualTo(
            Map.of( "name", "test", "bigNumber", (double) Long.MAX_VALUE, "integer", 10, "double", Double.MAX_VALUE, "boolean", true,
                    "list", List.of( "a", "b" ) ) );
    }

    @Test
    void toRawJs_single()
    {
        assertThat( GenericValue.stringValue( "a" ).toRawJs() ).isEqualTo( "a" );
        assertThat( GenericValue.numberValue( Double.MAX_VALUE ).toRawJs() ).isEqualTo( Double.MAX_VALUE );
        assertThat( GenericValue.numberValue( Long.MAX_VALUE ).toRawJs() ).isEqualTo( (double) Long.MAX_VALUE );
    }

    @Test
    void property_nestedObject()
    {
        final GenericValue nested =
            GenericValue.newObject().put( "inner", GenericValue.newObject().put( "value", "deep" ).build() ).build();

        assertEquals( "deep", nested.property( "inner" ).property( "value" ).asString() );
    }

    @Test
    void optional_incompatible()
    {
        assertFalse( GenericValue.booleanValue( false ).optional( "any" ).isPresent() );
    }

    @Test
    void values()
    {
        final GenericValue value = GenericValue.fromRawJava( List.of( "a", "b" ) );

        assertEquals( List.of( GenericValue.stringValue( "a" ), GenericValue.stringValue( "b" ) ), value.values() );
    }

    @Test
    void values_wrap_single()
    {
        assertThat( GenericValue.booleanValue( true ).values() ).containsExactly( GenericValue.booleanValue( true ) );
    }

    @Test
    void fromRawJava_string()
    {
        assertEquals( GenericValue.stringValue( "test" ), GenericValue.fromRawJava( "test" ) );
    }

    @Test
    void fromRawJava_boolean()
    {
        assertEquals( GenericValue.booleanValue( true ), GenericValue.fromRawJava( true ) );
        assertEquals( GenericValue.booleanValue( false ), GenericValue.fromRawJava( false ) );
    }

    @Test
    void fromRawJava_byte()
    {
        assertEquals( GenericValue.numberValue( 10 ), GenericValue.fromRawJava( (byte) 10 ) );
    }

    @Test
    void fromRawJava_short()
    {
        assertEquals( GenericValue.numberValue( 100 ), GenericValue.fromRawJava( (short) 100 ) );
    }

    @Test
    void fromRawJava_integer()
    {
        assertEquals( GenericValue.numberValue( 42 ), GenericValue.fromRawJava( 42 ) );
    }

    @Test
    void fromRawJava_long()
    {
        assertEquals( GenericValue.numberValue( Long.MAX_VALUE ), GenericValue.fromRawJava( Long.MAX_VALUE ) );
    }

    @Test
    void fromRawJava_float()
    {
        assertEquals( GenericValue.numberValue( 3.14f ), GenericValue.fromRawJava( 3.14f ) );
    }

    @Test
    void fromRawJava_double()
    {
        assertEquals( GenericValue.numberValue( 3.14159 ), GenericValue.fromRawJava( 3.14159 ) );
    }

    @Test
    void fromRawJava_collection()
    {
        final List<Object> list = List.of( "a", "b", "c" );
        final GenericValue result = GenericValue.fromRawJava( list );

        assertEquals( GenericValue.Type.LIST, result.getType() );
        assertThat( result.values() ).containsExactly( GenericValue.stringValue( "a" ), GenericValue.stringValue( "b" ),
                                                       GenericValue.stringValue( "c" ) );
    }

    @Test
    void fromRawJava_nestedStructure()
    {
        final Map<String, Object> nested = Map.of( "bool", true, "list", List.of( 1, 2, 3 ), "obj", Map.of( "key", "value" ) );
        final GenericValue result = GenericValue.fromRawJava( nested );

        assertEquals( GenericValue.Type.OBJECT, result.getType() );
        assertEquals( GenericValue.Type.BOOLEAN, result.property( "bool" ).getType() );
        assertEquals( GenericValue.Type.LIST, result.property( "list" ).getType() );
        assertEquals( GenericValue.Type.OBJECT, result.property( "obj" ).getType() );
    }

    @Test
    void fromRawJava_unsupportedType()
    {
        assertThrows( IllegalArgumentException.class, () -> GenericValue.fromRawJava( new Object() ) );
        assertThrows( IllegalArgumentException.class, () -> GenericValue.fromRawJava( new StringBuilder() ) );
    }

    @Test
    void objectBuilder_empty()
    {
        final GenericValue value = GenericValue.newObject().build();
        assertEquals( GenericValue.Type.OBJECT, value.getType() );
        assertTrue( value.properties().isEmpty() );
    }

    @Test
    void objectBuilder_putString()
    {
        final GenericValue obj = GenericValue.newObject().put( "key", "value" ).build();

        assertEquals( GenericValue.stringValue( "value" ), obj.property( "key" ) );
    }

    @Test
    void objectBuilder_putLong()
    {
        final GenericValue obj = GenericValue.newObject().put( "key", 100L ).build();

        assertEquals( GenericValue.numberValue( 100L ), obj.property( "key" ) );
    }

    @Test
    void objectBuilder_putDouble()
    {
        final GenericValue obj = GenericValue.newObject().put( "key", 10.5 ).build();

        assertEquals( GenericValue.numberValue( 10.5 ), obj.property( "key" ) );
    }

    @Test
    void objectBuilder_putBoolean()
    {
        final GenericValue obj = GenericValue.newObject().put( "key", true ).build();

        assertEquals( GenericValue.booleanValue( true ), obj.property( "key" ) );
    }

    @Test
    void objectBuilder_putGenericValue()
    {
        final GenericValue nested = GenericValue.newList().add( GenericValue.stringValue( "test" ) ).build();
        final GenericValue obj = GenericValue.newObject().put( "key", nested ).build();

        assertEquals( nested, obj.property( "key" ) );
    }

    @Test
    void listBuilder_empty()
    {
        final GenericValue value = GenericValue.newList().build();
        assertEquals( GenericValue.Type.LIST, value.getType() );
        assertTrue( value.values().isEmpty() );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( GenericValue.class ).verify();
    }
}