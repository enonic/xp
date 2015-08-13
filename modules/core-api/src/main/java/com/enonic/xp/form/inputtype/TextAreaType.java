package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

final class TextAreaType
    extends InputType
{
    public final static TextAreaType INSTANCE = new TextAreaType();

    private TextAreaType()
    {
        super( InputTypeName.TEXT_AREA );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        validateNotBlank( property );
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newString( value );
    }

    @Override
    public void checkValidity( final InputTypeConfig config, final Property property )
    {
        validateType( property, ValueTypes.STRING );
    }
}
