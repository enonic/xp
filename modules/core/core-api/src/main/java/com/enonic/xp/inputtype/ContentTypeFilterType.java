package com.enonic.xp.inputtype;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

@PublicApi
final class ContentTypeFilterType
    extends InputTypeBase
{
    public final static ContentTypeFilterType INSTANCE = new ContentTypeFilterType();

    private ContentTypeFilterType()
    {
        super( InputTypeName.CONTENT_TYPE_FILTER );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newString( value.asString() );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.STRING );
    }
}
