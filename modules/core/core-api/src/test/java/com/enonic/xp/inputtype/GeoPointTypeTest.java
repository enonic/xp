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
        final InputTypeConfig config = InputTypeConfig.create().build();
        final Value value = this.type.createValue( ValueFactory.newString( "1,2" ), config );

        assertNotNull( value );
        assertSame( ValueTypes.GEO_POINT, value.getType() );
    }

    @Test
    void testCreateDefaultValue()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.GEO_POINT, "41.387588,2.169994" ).build();

        final Value value = this.type.createDefaultValue( input );

        assertNotNull( value );
        assertEquals( "41.387588,2.169994", value.toString() );

    }

    @Test
    void testCreateDefaultValue_invalid()
    {
        final Input input = getDefaultInputBuilder( InputTypeName.GEO_POINT, "41.387588;2.169994" ).build();
        assertThrows(IllegalArgumentException.class, () -> this.type.createDefaultValue( input ) );
    }

    @Test
    void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( geoPointProperty( "1,2" ), config );
    }

    @Test
    void testValidate_invalidType()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        assertThrows(InputTypeValidationException.class, () -> this.type.validate( booleanProperty( true ), config ));
    }
}
