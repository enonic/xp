package com.enonic.xp.inputtype;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

@Beta
final class PageControllerType
    extends InputTypeBase
{
    public final static PageControllerType INSTANCE = new PageControllerType();

    private PageControllerType()
    {
        super( InputTypeName.PAGE_CONTROLLER );
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
