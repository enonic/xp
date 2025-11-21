package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.util.GenericValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LongTypeTest
    extends BaseInputTypeTest
{
    public LongTypeTest()
    {
        super( LongType.INSTANCE );
    }

    @Test
    void testName()
    {
        assertEquals( "Long", this.type.getName().toString() );
    }

    @Test
    void testToString()
    {
        assertEquals( "Long", this.type.toString() );
    }

    @Test
    void testCreateProperty()
    {
        final Value value = this.type.createValue( ValueFactory.newDouble( 13.0 ), GenericValue.object().build() );
        assertNotNull( value );
        assertSame( ValueTypes.LONG, value.getType() );
    }

    @Test
    void testValidate()
    {
        this.type.validate( longProperty( 13 ), GenericValue.object().build() );
    }

    @Test
    void testValidate_invalidType()
    {
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), GenericValue.object().build() ) );
    }

    @Test
    void testValidate_invalidMin()
    {
        final GenericValue config = GenericValue.object().put( "min", GenericValue.longValue( 5 ) ).build();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( longProperty( 2 ), config ) );
    }

    @Test
    void testValidate_invalidMax()
    {
        final GenericValue config = GenericValue.object().put( "max", GenericValue.longValue( 5 ) ).build();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( longProperty( 7 ), config ) );
    }

}
