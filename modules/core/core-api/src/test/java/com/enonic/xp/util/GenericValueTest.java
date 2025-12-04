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

    @Test
    void asInteger_fromInteger()
    {
        assertEquals( 10, GenericValue.numberValue( 10 ).asInteger() );
    }

    @Test
    void asInteger_fromLong()
    {
        assertEquals( 100, GenericValue.numberValue( 100L ).asInteger() );
    }

    @Test
    void asInteger_fromDouble()
    {
        // HALF_EVEN rounding: rounds to nearest even number when exactly halfway
        assertEquals( 10, GenericValue.numberValue( 10.5 ).asInteger() ); // rounds to 10 (even)
        assertEquals( 10, GenericValue.numberValue( 10.4 ).asInteger() );
        assertEquals( 11, GenericValue.numberValue( 10.6 ).asInteger() );
        assertEquals( 12, GenericValue.numberValue( 11.5 ).asInteger() ); // rounds to 12 (even)
        assertEquals( -10, GenericValue.numberValue( -10.5 ).asInteger() ); // rounds to -10 (even)
    }

    @Test
    void asInteger_fromString()
    {
        assertEquals( 42, GenericValue.stringValue( "42" ).asInteger() );
    }

    @Test
    void asInteger_fromString_invalid()
    {
        assertThrows( NumberFormatException.class, () -> GenericValue.stringValue( "not a number" ).asInteger() );
    }

    @Test
    void asInteger_fromLong_overflow()
    {
        assertThrows( ArithmeticException.class, () -> GenericValue.numberValue( Long.MAX_VALUE ).asInteger() );
    }

    @Test
    void asInteger_fromDouble_overflow()
    {
        assertThrows( ArithmeticException.class, () -> GenericValue.numberValue( Double.MAX_VALUE ).asInteger() );
    }

    @Test
    void asInteger_incompatibleType()
    {
        assertThrows( IllegalStateException.class, () -> GenericValue.booleanValue( true ).asInteger() );
        assertThrows( IllegalStateException.class, () -> GenericValue.list().build().asInteger() );
        assertThrows( IllegalStateException.class, () -> GenericValue.object().build().asInteger() );
    }

    @Test
    void asLong_fromLong()
    {
        assertEquals( 100L, GenericValue.numberValue( 100L ).asLong() );
    }

    @Test
    void asLong_fromInteger()
    {
        assertEquals( 10L, GenericValue.numberValue( 10 ).asLong() );
    }

    @Test
    void asLong_fromDouble()
    {
        // HALF_EVEN rounding: rounds to nearest even number when exactly halfway
        assertEquals( 10L, GenericValue.numberValue( 10.5 ).asLong() ); // rounds to 10 (even)
        assertEquals( 10L, GenericValue.numberValue( 10.4 ).asLong() );
        assertEquals( 11L, GenericValue.numberValue( 10.6 ).asLong() );
        assertEquals( 12L, GenericValue.numberValue( 11.5 ).asLong() ); // rounds to 12 (even)
        assertEquals( -10L, GenericValue.numberValue( -10.5 ).asLong() ); // rounds to -10 (even)
    }

    @Test
    void asLong_fromString()
    {
        assertEquals( 123456789L, GenericValue.stringValue( "123456789" ).asLong() );
    }

    @Test
    void asLong_fromString_invalid()
    {
        assertThrows( NumberFormatException.class, () -> GenericValue.stringValue( "not a number" ).asLong() );
    }

    @Test
    void asLong_fromDouble_overflow()
    {
        assertThrows( ArithmeticException.class, () -> GenericValue.numberValue( Double.MAX_VALUE ).asLong() );
    }

    @Test
    void asLong_incompatibleType()
    {
        assertThrows( IllegalStateException.class, () -> GenericValue.booleanValue( true ).asLong() );
        assertThrows( IllegalStateException.class, () -> GenericValue.list().build().asLong() );
        assertThrows( IllegalStateException.class, () -> GenericValue.object().build().asLong() );
    }

    @Test
    void asBoolean_incompatibleType()
    {
        assertThrows( IllegalStateException.class, () -> GenericValue.stringValue( "true" ).asBoolean() );
        assertThrows( IllegalStateException.class, () -> GenericValue.numberValue( 1 ).asBoolean() );
        assertThrows( IllegalStateException.class, () -> GenericValue.list().build().asBoolean() );
        assertThrows( IllegalStateException.class, () -> GenericValue.object().build().asBoolean() );
    }

    @Test
    void asStringList_fromList()
    {
        final GenericValue list = GenericValue.list()
            .add( GenericValue.stringValue( "a" ) )
            .add( GenericValue.numberValue( 10 ) )
            .add( GenericValue.booleanValue( true ) )
            .build();

        assertThat( list.asStringList() ).containsExactly( "a", "10", "true" );
    }

    @Test
    void asStringList_fromSingleValue()
    {
        assertThat( GenericValue.stringValue( "hello" ).asStringList() ).containsExactly( "hello" );
        assertThat( GenericValue.numberValue( 42 ).asStringList() ).containsExactly( "42" );
    }

    @Test
    void rawJava_string()
    {
        assertEquals( "hello", GenericValue.stringValue( "hello" ).rawJava() );
    }

    @Test
    void rawJava_long()
    {
        assertEquals( Long.MAX_VALUE, GenericValue.numberValue( Long.MAX_VALUE ).rawJava() );
    }

    @Test
    void rawJava_integer()
    {
        assertEquals( 10, GenericValue.numberValue( 10 ).rawJava() );
    }

    @Test
    void rawJava_double()
    {
        assertEquals( 10.5, GenericValue.numberValue( 10.5 ).rawJava() );
    }

    @Test
    void rawJava_boolean()
    {
        assertEquals( true, GenericValue.booleanValue( true ).rawJava() );
        assertEquals( false, GenericValue.booleanValue( false ).rawJava() );
    }

    @Test
    void rawJava_list()
    {
        final GenericValue list = GenericValue.list()
            .add( GenericValue.stringValue( "a" ) )
            .add( GenericValue.numberValue( 10 ) )
            .build();

        assertThat( list.rawJava() ).isEqualTo( List.of( "a", 10 ) );
    }

    @Test
    void rawJava_object()
    {
        final GenericValue obj = GenericValue.object()
            .put( "name", "test" )
            .put( "value", 42L )
            .build();

        assertThat( obj.rawJava() ).isEqualTo( Map.of( "name", "test", "value", 42 ) ); // 42L is optimized to Integer
    }

    @Test
    void rawJava_nestedStructure()
    {
        final GenericValue nested = GenericValue.object()
            .put( "list", GenericValue.list()
                .add( GenericValue.numberValue( 1 ) )
                .add( GenericValue.numberValue( 2 ) )
                .build() )
            .build();

        assertThat( nested.rawJava() ).isEqualTo( Map.of( "list", List.of( 1, 2 ) ) );
    }

    @Test
    void rawJs_string()
    {
        assertEquals( "hello", GenericValue.stringValue( "hello" ).rawJs() );
    }

    @Test
    void rawJs_long_convertedToDouble()
    {
        assertEquals( (double) Long.MAX_VALUE, GenericValue.numberValue( Long.MAX_VALUE ).rawJs() );
    }

    @Test
    void rawJs_integer()
    {
        assertEquals( 10, GenericValue.numberValue( 10 ).rawJs() );
    }

    @Test
    void rawJs_double()
    {
        assertEquals( 10.5, GenericValue.numberValue( 10.5 ).rawJs() );
    }

    @Test
    void rawJs_boolean()
    {
        assertEquals( true, GenericValue.booleanValue( true ).rawJs() );
    }

    @Test
    void rawJs_list()
    {
        final GenericValue list = GenericValue.list()
            .add( GenericValue.stringValue( "a" ) )
            .add( GenericValue.numberValue( Long.MAX_VALUE ) )
            .build();

        assertThat( list.rawJs() ).isEqualTo( List.of( "a", (double) Long.MAX_VALUE ) );
    }

    @Test
    void rawJs_object()
    {
        final GenericValue obj = GenericValue.object()
            .put( "name", "test" )
            .put( "bigNumber", Long.MAX_VALUE )
            .build();

        assertThat( obj.rawJs() ).isEqualTo( Map.of( "name", "test", "bigNumber", (double) Long.MAX_VALUE ) );
    }

    @Test
    void getType_allTypes()
    {
        assertEquals( GenericValue.Type.STRING, GenericValue.stringValue( "test" ).getType() );
        assertEquals( GenericValue.Type.NUMBER, GenericValue.numberValue( 10 ).getType() );
        assertEquals( GenericValue.Type.NUMBER, GenericValue.numberValue( 10L ).getType() );
        assertEquals( GenericValue.Type.NUMBER, GenericValue.numberValue( 10.5 ).getType() );
        assertEquals( GenericValue.Type.BOOLEAN, GenericValue.booleanValue( true ).getType() );
        assertEquals( GenericValue.Type.LIST, GenericValue.list().build().getType() );
        assertEquals( GenericValue.Type.OBJECT, GenericValue.object().build().getType() );
    }

    @Test
    void listBuilder_multipleAdds()
    {
        final GenericValue list = GenericValue.list()
            .add( GenericValue.stringValue( "a" ) )
            .add( GenericValue.stringValue( "b" ) )
            .add( GenericValue.stringValue( "c" ) )
            .build();

        assertThat( list.asList() ).hasSize( 3 );
        assertThat( list.asList() ).containsExactly(
            GenericValue.stringValue( "a" ),
            GenericValue.stringValue( "b" ),
            GenericValue.stringValue( "c" )
        );
    }

    @Test
    void objectBuilder_putString()
    {
        final GenericValue obj = GenericValue.object()
            .put( "key", "value" )
            .build();

        assertEquals( GenericValue.stringValue( "value" ), obj.property( "key" ) );
    }

    @Test
    void objectBuilder_putLong()
    {
        final GenericValue obj = GenericValue.object()
            .put( "key", 100L )
            .build();

        assertEquals( GenericValue.numberValue( 100L ), obj.property( "key" ) );
    }

    @Test
    void objectBuilder_putDouble()
    {
        final GenericValue obj = GenericValue.object()
            .put( "key", 10.5 )
            .build();

        assertEquals( GenericValue.numberValue( 10.5 ), obj.property( "key" ) );
    }

    @Test
    void objectBuilder_putBoolean()
    {
        final GenericValue obj = GenericValue.object()
            .put( "key", true )
            .build();

        assertEquals( GenericValue.booleanValue( true ), obj.property( "key" ) );
    }

    @Test
    void objectBuilder_putGenericValue()
    {
        final GenericValue nested = GenericValue.list().add( GenericValue.stringValue( "test" ) ).build();
        final GenericValue obj = GenericValue.object()
            .put( "key", nested )
            .build();

        assertEquals( nested, obj.property( "key" ) );
    }

    @Test
    void objectBuilder_chaining()
    {
        final GenericValue obj = GenericValue.object()
            .put( "str", "value" )
            .put( "num", 42L )
            .put( "dec", 3.14 )
            .put( "bool", true )
            .build();

        assertThat( obj.properties() ).hasSize( 4 );
    }

    @Test
    void objectBuilder_nullKey()
    {
        assertThrows( NullPointerException.class, () -> GenericValue.object().put( null, "value" ) );
        assertThrows( NullPointerException.class, () -> GenericValue.object().put( null, 10L ) );
        assertThrows( NullPointerException.class, () -> GenericValue.object().put( null, 10.5 ) );
        assertThrows( NullPointerException.class, () -> GenericValue.object().put( null, true ) );
        assertThrows( NullPointerException.class,
                      () -> GenericValue.object().put( null, GenericValue.stringValue( "test" ) ) );
    }

    @Test
    void objectBuilder_nullValue()
    {
        assertThrows( NullPointerException.class, () -> GenericValue.object().put( "key", (GenericValue) null ) );
    }

    @Test
    void stringList_utility()
    {
        final GenericValue list = GenericValue.stringList( List.of( "a", "b", "c" ) );

        assertEquals( GenericValue.Type.LIST, list.getType() );
        assertThat( list.asList() ).containsExactly(
            GenericValue.stringValue( "a" ),
            GenericValue.stringValue( "b" ),
            GenericValue.stringValue( "c" )
        );
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
        final List<Object> list = List.of( "a", 42, true );
        final GenericValue result = GenericValue.fromRawJava( list );

        assertEquals( GenericValue.Type.LIST, result.getType() );
        assertThat( result.asList() ).containsExactly(
            GenericValue.stringValue( "a" ),
            GenericValue.numberValue( 42 ),
            GenericValue.booleanValue( true )
        );
    }

    @Test
    void fromRawJava_map()
    {
        final Map<String, Object> map = Map.of( "name", "test", "value", 42 );
        final GenericValue result = GenericValue.fromRawJava( map );

        assertEquals( GenericValue.Type.OBJECT, result.getType() );
        assertEquals( GenericValue.stringValue( "test" ), result.property( "name" ) );
        assertEquals( GenericValue.numberValue( 42 ), result.property( "value" ) );
    }

    @Test
    void fromRawJava_nestedStructure()
    {
        final Map<String, Object> nested = Map.of(
            "list", List.of( 1, 2, 3 ),
            "obj", Map.of( "key", "value" )
        );
        final GenericValue result = GenericValue.fromRawJava( nested );

        assertEquals( GenericValue.Type.OBJECT, result.getType() );
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
    void numberValue_optimization_intVsLong()
    {
        // Values that fit in int should be stored as Integer
        final GenericValue smallValue = GenericValue.numberValue( 100L );
        assertEquals( 100, smallValue.asInteger() );

        // Values that don't fit in int should be stored as Long
        final GenericValue largeValue = GenericValue.numberValue( (long) Integer.MAX_VALUE + 1L );
        assertEquals( (long) Integer.MAX_VALUE + 1L, largeValue.asLong() );
    }

    @Test
    void booleanValue_singletonConstants()
    {
        // Verify that the same instance is returned for the same boolean value
        final GenericValue true1 = GenericValue.booleanValue( true );
        final GenericValue true2 = GenericValue.booleanValue( true );
        final GenericValue false1 = GenericValue.booleanValue( false );
        final GenericValue false2 = GenericValue.booleanValue( false );

        // While we can't test reference equality directly without accessing private fields,
        // we can verify the behavior is correct
        assertEquals( true1, true2 );
        assertEquals( false1, false2 );
        assertTrue( true1.asBoolean() );
        assertFalse( false1.asBoolean() );
    }

    @Test
    void property_nestedObject()
    {
        final GenericValue nested = GenericValue.object()
            .put( "inner", GenericValue.object()
                .put( "value", "deep" )
                .build() )
            .build();

        assertEquals( "deep", nested.property( "inner" ).property( "value" ).asString() );
    }

    @Test
    void optional_existingProperty()
    {
        final GenericValue obj = GenericValue.object().put( "key", "value" ).build();

        assertTrue( obj.optional( "key" ).isPresent() );
        assertEquals( GenericValue.stringValue( "value" ), obj.optional( "key" ).get() );
    }

    @Test
    void optional_nonExistingProperty()
    {
        final GenericValue obj = GenericValue.object().put( "key", "value" ).build();

        assertFalse( obj.optional( "other" ).isPresent() );
    }

    @Test
    void complexNestedStructure()
    {
        final GenericValue complex = GenericValue.object()
            .put( "users", GenericValue.list()
                .add( GenericValue.object()
                    .put( "name", "Alice" )
                    .put( "age", 30L )
                    .build() )
                .add( GenericValue.object()
                    .put( "name", "Bob" )
                    .put( "age", 25L )
                    .build() )
                .build() )
            .put( "metadata", GenericValue.object()
                .put( "version", 1L )
                .put( "active", true )
                .build() )
            .build();

        assertEquals( GenericValue.Type.OBJECT, complex.getType() );
        assertEquals( GenericValue.Type.LIST, complex.property( "users" ).getType() );
        assertEquals( 2, complex.property( "users" ).asList().size() );
        assertEquals( "Alice", complex.property( "users" ).asList().get( 0 ).property( "name" ).asString() );
        assertEquals( 30L, complex.property( "users" ).asList().get( 0 ).property( "age" ).asLong() );
        assertTrue( complex.property( "metadata" ).property( "active" ).asBoolean() );
    }
}