package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

final class ContentSelector
    extends ConfigurableInputType<ContentSelectorConfig>
{
    public ContentSelector()
    {
        super( InputTypeName.CONTENT_SELECTOR, new ContentSelectorConfigSerializer() );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        validateNotBlank( property );
    }

    @Override
    public void checkTypeValidity( final Property property )
    {
        validateType( property, ValueTypes.REFERENCE );
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newReference( ValueTypes.REFERENCE.convert( value ) );
    }
}
