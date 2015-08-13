package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

final class TextLineType
    extends InputTypeBase
{
    public final static TextLineType INSTANCE = new TextLineType();

    private TextLineType()
    {
        super( InputTypeName.TEXT_LINE );
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
