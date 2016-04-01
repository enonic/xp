package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypeException;
import com.enonic.xp.data.ValueTypes;

final class DateType
    extends InputTypeBase
{
    public final static DateType INSTANCE = new DateType();

    private DateType()
    {
        super( InputTypeName.DATE );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return ValueFactory.newLocalDate( ValueTypes.LOCAL_DATE.convert( value ) );
    }

    @Override
    public Value createDefaultValue( final InputTypeConfig defaultConfig )
    {
        final InputTypeProperty defaultProperty = defaultConfig.getProperty( "default" );
        if ( defaultProperty != null )
        {
            try
            {
                return ValueFactory.newLocalDate( ValueTypes.LOCAL_DATE.convert( defaultProperty.getValue() ) );
            }
            catch ( ValueTypeException e )
            {
                throw new IllegalArgumentException("Invalid Date format: " + defaultProperty.getValue());
            }
        }
        return super.createDefaultValue( defaultConfig );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.LOCAL_DATE );
    }
}
