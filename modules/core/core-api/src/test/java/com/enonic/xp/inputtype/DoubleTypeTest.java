package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DoubleTypeTest
    extends BaseInputTypeTest
{
    public DoubleTypeTest()
    {
        super( DoubleType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "Double", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "Double", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final Value value = this.type.createValue( ValueFactory.newDouble( 1.3 ), GenericValue.object().build() );
        assertNotNull( value );
        assertSame( ValueTypes.DOUBLE, value.getType() );
    }

    @Test
    public void testValidate()
    {
        this.type.validate( doubleProperty( 1.3 ), GenericValue.object().build() );
    }

    @Test
    public void testValidate_invalidType()
    {
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), GenericValue.object().build() ) );
    }

    @Test
    public void testValidate_invalidMin()
    {
        final GenericValue config = GenericValue.object().put( "min", 5.0 ).build();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( doubleProperty( 2.4 ), config ) );
    }

    @Test
    public void testValidate_invalidMax()
    {
        final GenericValue config = GenericValue.object().put( "max", 5.0 ).build();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( doubleProperty( 7.3 ), config ) );
    }
}
