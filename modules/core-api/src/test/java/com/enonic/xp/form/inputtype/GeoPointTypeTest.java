package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidTypeException;

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
        final Value value = this.type.createPropertyValue( "1,2", config );

        assertNotNull( value );
        assertSame( ValueTypes.GEO_POINT, value.getType() );
    }

    @Test
    public void testContract()
    {
        this.type.checkBreaksRequiredContract( geoPointProperty( "1,2" ) );
    }

    @Test
    public void testCheckValidity()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.checkValidity( config, geoPointProperty( "1,2" ) );
    }

    @Test(expected = InvalidTypeException.class)
    public void testCheckValidity_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.checkValidity( config, booleanProperty( true ) );
    }
}
