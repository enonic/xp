package com.enonic.xp.inputtype;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypeException;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

@PublicApi
final class InstantType
    extends InputTypeBase
{
    public static final InstantType INSTANCE = new InstantType();

    private InstantType()
    {
        super( InputTypeName.INSTANT );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newDateTime( value.asInstant() );
    }

    @Override
    public Value createDefaultValue( final Input input )
    {
        final String defaultValue = input.getDefaultValue().getRootValue();

        if ( defaultValue != null )
        {
            return parseDateTime( defaultValue );
        }

        return super.createDefaultValue( input );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.DATE_TIME );
    }

    private Value parseDateTime( final String value )
    {
        try
        {
            return ValueFactory.newDateTime( ValueTypes.DATE_TIME.convert( value ) );
        }
        catch ( ValueTypeException e )
        {
            throw new IllegalArgumentException( String.format( "Invalid Instant format: %s", value ) );
        }
    }
}
