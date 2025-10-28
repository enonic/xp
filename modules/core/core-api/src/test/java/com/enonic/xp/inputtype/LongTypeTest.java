package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

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
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( ValueFactory.newDouble( 13.0 ), config );
        assertNotNull( value );
        assertSame( ValueTypes.LONG, value.getType() );
    }

    @Test
    void testCreateDefaultValue()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.LONG, "2" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertEquals( 2L, value.asLong().longValue() );

    }

    @Test
    void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( longProperty( 13 ), config );
    }

    @Test
    void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ));
    }

    @Test
    void testValidate_invalidMin()
    {
        final InputTypeConfig config = InputTypeConfig.create().property( InputTypeProperty.create( "min", "5" ).build( )).build();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( longProperty( 2 ), config ));
    }

    @Test
    void testValidate_invalidMax()
    {
        final InputTypeConfig config = InputTypeConfig.create().property( InputTypeProperty.create( "max", "5" ).build( )).build();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( longProperty( 7 ), config ));
    }

}
