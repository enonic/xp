package com.enonic.xp.form.inputtype;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

@Beta
final class ContentTypeFilterType
    extends InputTypeBase
{
    public final static ContentTypeFilterType INSTANCE = new ContentTypeFilterType();

    private ContentTypeFilterType()
    {
        super( InputTypeName.CONTENT_TYPE_FILTER );
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
