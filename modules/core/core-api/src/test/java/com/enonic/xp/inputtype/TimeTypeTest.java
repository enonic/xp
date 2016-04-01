package com.enonic.xp.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

import static org.junit.Assert.*;

public class TimeTypeTest
    extends BaseInputTypeTest
{
    public TimeTypeTest()
    {
        super( TimeType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "Time", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "Time", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( "22:11:00", config );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_TIME, value.getType() );
    }

    @Test
    public void testCreateDefaultValue()
    {
        final InputTypeDefault config = InputTypeDefault.create().
            property( InputTypeProperty.create( "default", "08:08:08" ).
                build() ).
            build();

        final Value value = this.type.createDefaultValue( config );

        assertNotNull( value );
        assertSame( ValueTypes.LOCAL_TIME, value.getType() );
        assertEquals( value.toString(), "08:08:08" );

    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateDefaultValue_invalid()
    {
        final InputTypeDefault config = InputTypeDefault.create().
            property( InputTypeProperty.create( "default", "25:08:08" ).
                build() ).
            build();

        this.type.createDefaultValue( config );
    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( localTimeProperty(), config );
    }

    @Test(expected = InputTypeValidationException.class)
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( booleanProperty( true ), config );
    }
}
