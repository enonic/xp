package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

final class ImageSelectorType
    extends InputTypeBase
{
    public static final ImageSelectorType INSTANCE = new ImageSelectorType();

    private ImageSelectorType()
    {
        super( InputTypeName.IMAGE_SELECTOR );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newReference( value.asReference() );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.REFERENCE );
    }
}
