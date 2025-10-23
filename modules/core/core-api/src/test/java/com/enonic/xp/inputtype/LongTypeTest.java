package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LongTypeTest
    extends BaseInputTypeTest
{
    public LongTypeTest()
    {
        super( LongType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "Long", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "Long", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final Value value = this.type.createValue( ValueFactory.newDouble( 13.0 ), GenericValue.object().build() );
        assertNotNull( value );
        assertSame( ValueTypes.LONG, value.getType() );
    }

    @Test
    public void testValidate()
    {
        this.type.validate( longProperty( 13 ), GenericValue.object().build() );
    }

    @Test
    public void testValidate_invalidType()
    {
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), GenericValue.object().build() ) );
    }

    @Test
    public void testValidate_invalidMin()
    {
        final GenericValue config = GenericValue.object().put( "min", GenericValue.longValue( 5 ) ).build();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( longProperty( 2 ), config ) );
    }

    @Test
    public void testValidate_invalidMax()
    {
        final GenericValue config = GenericValue.object().put( "max", GenericValue.longValue( 5 ) ).build();
        assertThrows( InputTypeValidationException.class, () -> this.type.validate( longProperty( 7 ), config ) );
    }

}
