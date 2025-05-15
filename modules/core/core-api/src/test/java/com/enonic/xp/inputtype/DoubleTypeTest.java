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
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( ValueFactory.newDouble( 1.3 ), config );
        assertNotNull( value );
        assertSame( ValueTypes.DOUBLE, value.getType() );
    }

    @Test
    public void testCreateDefaultValue()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.DOUBLE, "1.3" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertEquals( 1.3D, value.asDouble(), Double.MIN_NORMAL );
    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( doubleProperty( 1.3 ), config );
    }

    @Test
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ));
    }

    @Test
    public void testValidate_invalidMin()
    {
        final InputTypeConfig config = InputTypeConfig.create().property( InputTypeProperty.create( "min", "5.0" ).build( )).build();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( doubleProperty( 2.4 ), config ));
    }

    @Test
    public void testValidate_invalidMax()
    {
        final InputTypeConfig config = InputTypeConfig.create().property( InputTypeProperty.create( "max", "5.0" ).build( )).build();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( doubleProperty( 7.3 ), config ));
    }
}
