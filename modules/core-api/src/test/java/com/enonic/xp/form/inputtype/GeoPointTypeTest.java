package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

import static org.junit.Assert.*;

public class GeoPointTypeTest
    extends BaseInputTypeTest
{
    public GeoPointTypeTest()
    {
        super( GeoPointType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "GeoPoint", this.type.getName() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "GeoPoint", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( "1,2", config );

        assertNotNull( value );
        assertSame( ValueTypes.GEO_POINT, value.getType() );
    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( geoPointProperty( "1,2" ), config );
    }

    @Test(expected = InputTypeValidationException.class)
    public void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( booleanProperty( true ), config );
    }
}
