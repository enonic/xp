package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.util.Reference;

final class ImageSelectorType
    extends InputType
{
    public final static ImageSelectorType INSTANCE = new ImageSelectorType();

    private ImageSelectorType()
    {
        super( InputTypeName.IMAGE_SELECTOR );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
    }

    @Override
    public void checkTypeValidity( final Property property )
    {
        validateType( property, ValueTypes.REFERENCE );
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newReference( Reference.from( value ) );
    }
}
