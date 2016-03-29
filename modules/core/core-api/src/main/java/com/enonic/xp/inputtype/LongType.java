package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

final class LongType
    extends InputTypeBase
{
    public final static LongType INSTANCE = new LongType();

    private LongType()
    {
        super( InputTypeName.LONG );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return ValueFactory.newLong( ValueTypes.LONG.convert( value ) );
    }

    @Override
    public Value createDefaultValue( final InputTypeConfig defaultConfig )
    {
        final InputTypeProperty defaultProperty = defaultConfig.getProperty( "default" );
        if ( defaultProperty != null )
        {
            final String defaultValue = defaultProperty.getValue();

            return ValueFactory.newLong( Long.valueOf( defaultValue ) );
        }
        return super.createDefaultValue( defaultConfig );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.LONG );
    }
}
