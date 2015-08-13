package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

final class ContentSelectorType
    extends InputType
{
    public final static ContentSelectorType INSTANCE = new ContentSelectorType();

    private ContentSelectorType()
    {
        super( InputTypeName.CONTENT_SELECTOR );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        validateNotBlank( property );
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newReference( ValueTypes.REFERENCE.convert( value ) );
    }

    @Override
    public void checkValidity( final InputTypeConfig config, final Property property )
    {
        validateType( property, ValueTypes.REFERENCE );
    }
}

