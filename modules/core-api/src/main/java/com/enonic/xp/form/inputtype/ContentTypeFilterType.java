package com.enonic.xp.form.inputtype;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

@Beta
final class ContentTypeFilterType
    extends InputType
{
    public final static ContentTypeFilterType INSTANCE = new ContentTypeFilterType();

    private ContentTypeFilterType()
    {
        super( InputTypeName.CONTENT_TYPE_FILTER );
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
