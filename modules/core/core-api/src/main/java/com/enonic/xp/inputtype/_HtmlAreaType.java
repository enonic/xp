package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

final class _HtmlAreaType
    extends InputTypeBase
{
    public final static _HtmlAreaType INSTANCE = new _HtmlAreaType();

    private _HtmlAreaType()
    {
        super( InputTypeName._HTML_AREA );
    }

    @Override
    public Value createDefaultValue( final Input input )
    {
        final String rootValue = input.getDefaultValue().getRootValue();
        if ( rootValue != null )
        {
            return ValueFactory.newString( rootValue );
        }
        return super.createDefaultValue( input );
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
