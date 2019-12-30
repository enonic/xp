package com.enonic.xp.inputtype;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueType;
import com.enonic.xp.form.Input;

@PublicApi
public abstract class InputTypeBase
    implements InputType
{
    private final InputTypeName name;

    protected InputTypeBase( final InputTypeName name )
    {
        this.name = name;
    }

    @Override
    public final InputTypeName getName()
    {
        return name;
    }

    @Override
    public final String toString()
    {
        return this.name.toString();
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return createValue( ValueFactory.newString( value ), config );
    }

    @Override
    public abstract Value createValue( final Value value, final InputTypeConfig config );

    @Override
    public Value createDefaultValue( final Input input )
    {
        return null;
    }

    protected final void validateType( final Property property, final ValueType expectedType )
    {
        final ValueType actualType = property.getType();
        if ( !actualType.equals( expectedType ) )
        {
            throw InputTypeValidationException.invalidType( property, actualType, expectedType );
        }
    }

    protected final void validateType( final Property property, final ValueType... expectedTypes )
    {
        final ValueType actualType = property.getType();
        if ( !inSet( actualType, expectedTypes ) )
        {
            throw InputTypeValidationException.invalidType( property, expectedTypes );
        }
    }

    protected final void validateValue( final Property property, final boolean flag, final String message )
    {
        if ( !flag )
        {
            throw InputTypeValidationException.invalidValue( property, message );
        }
    }

    private static boolean inSet( final ValueType check, final ValueType... types )
    {
        for ( final ValueType type : types )
        {
            if ( type.equals( check ) )
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public abstract void validate( Property property, InputTypeConfig config );
}
