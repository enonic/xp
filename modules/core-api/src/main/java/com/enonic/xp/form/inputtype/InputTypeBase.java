package com.enonic.xp.form.inputtype;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueType;
import com.enonic.xp.form.InvalidTypeException;

@Beta
public abstract class InputTypeBase
    implements InputType
{
    private final InputTypeName name;

    protected InputTypeBase( final InputTypeName name )
    {
        this.name = name;
    }

    @Override
    public final String getName()
    {
        return name.toString();
    }

    @Override
    public final String toString()
    {
        return this.name.toString();
    }

    public abstract Value createValue( final String value, final InputTypeConfig config );

    protected final void validateType( final Property property, final ValueType type )
    {
        if ( property.getType() != type )
        {
            throw new InvalidTypeException( property, type );
        }
    }

    public abstract void validate( Property property, InputTypeConfig config );
}
