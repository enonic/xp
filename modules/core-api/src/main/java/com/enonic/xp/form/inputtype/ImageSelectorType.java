package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.util.Reference;

final class ImageSelectorType
    extends InputTypeBase
{
    public final static ImageSelectorType INSTANCE = new ImageSelectorType();

    private ImageSelectorType()
    {
        super( InputTypeName.IMAGE_SELECTOR );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return Value.newReference( Reference.from( value ) );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.REFERENCE );
    }
}
