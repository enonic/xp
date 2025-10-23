package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

final class MediaSelectorType
    extends InputTypeBase
{
    public static final MediaSelectorType INSTANCE = new MediaSelectorType();

    private MediaSelectorType()
    {
        super( InputTypeName.MEDIA_SELECTOR );
    }

    @Override
    public Value createValue( final Value value, final GenericValue config )
    {
        return ValueFactory.newReference( value.asReference() );
    }

    @Override
    public void validate( final Property property, final GenericValue config )
    {
        validateType( property, ValueTypes.REFERENCE );
    }
}
