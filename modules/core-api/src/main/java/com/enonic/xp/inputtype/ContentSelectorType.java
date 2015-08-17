package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

final class ContentSelectorType
    extends InputTypeBase
{
    public final static ContentSelectorType INSTANCE = new ContentSelectorType();

    private ContentSelectorType()
    {
        super( InputTypeName.CONTENT_SELECTOR );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return ValueFactory.newReference( ValueTypes.REFERENCE.convert( value ) );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.REFERENCE );
    }
}

