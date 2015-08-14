package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

final class TagType
    extends InputTypeBase
{
    public final static TagType INSTANCE = new TagType();

    private TagType()
    {
        super( InputTypeName.TAG );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return Value.newString( value );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.STRING );
    }
}

