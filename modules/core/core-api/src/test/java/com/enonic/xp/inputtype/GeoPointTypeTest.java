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

class GeoPointTypeTest
    extends BaseInputTypeTest
{
    public GeoPointTypeTest()
    {
        super( GeoPointType.INSTANCE );
    }

    @Test
    void testName()
    {
        assertEquals( "GeoPoint", this.type.getName().toString() );
    }

    @Test
    void testToString()
    {
        assertEquals( "GeoPoint", this.type.toString() );
    }

    @Test
    void testCreateProperty()
    {
        final Value value = this.type.createValue( ValueFactory.newString( "1,2" ), GenericValue.object().build() );

        assertNotNull( value );
        assertSame( ValueTypes.GEO_POINT, value.getType() );
    }

    @Test
    void testValidate()
    {
        this.type.validate( geoPointProperty( "1,2" ), GenericValue.object().build() );
    }

    @Test
    void testValidate_invalidType()
    {
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), GenericValue.object().build() ));
    }
}
