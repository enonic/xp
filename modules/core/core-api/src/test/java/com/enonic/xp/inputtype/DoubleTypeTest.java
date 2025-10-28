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

class DoubleTypeTest
    extends BaseInputTypeTest
{
    public DoubleTypeTest()
    {
        super( DoubleType.INSTANCE );
    }

    @Test
    void testName()
    {
        assertEquals( "Double", this.type.getName().toString() );
    }

    @Test
    void testToString()
    {
        assertEquals( "Double", this.type.toString() );
    }

    @Test
    void testCreateProperty()
    {
        final Value value = this.type.createValue( ValueFactory.newDouble( 1.3 ), GenericValue.object().build() );
        assertNotNull( value );
        assertSame( ValueTypes.DOUBLE, value.getType() );
    }

    @Test
    void testValidate()
    {
        this.type.validate( doubleProperty( 1.3 ), GenericValue.object().build() );
    }

    @Test
    void testValidate_invalidType()
    {
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), GenericValue.object().build() ) );
    }

    @Test
    void testValidate_invalidMin()
    {
        final GenericValue config = GenericValue.object().put( "min", 5.0 ).build();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( doubleProperty( 2.4 ), config ) );
    }

    @Test
    void testValidate_invalidMax()
    {
        final GenericValue config = GenericValue.object().put( "max", 5.0 ).build();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( doubleProperty( 7.3 ), config ) );
    }
}
