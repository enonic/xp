package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

final class HtmlAreaType
    extends InputTypeBase
{
    public final static HtmlAreaType INSTANCE = new HtmlAreaType();

    private HtmlAreaType()
    {
        super( InputTypeName.HTML_AREA );
    }

    @Override
    public Value createDefaultValue( final InputTypeConfig defaultConfig )
    {
        final InputTypeProperty defaultProperty = defaultConfig.getProperty( "default" );
        if ( defaultProperty != null )
        {
            return ValueFactory.newString( defaultProperty.getValue() );
        }
        return super.createDefaultValue( defaultConfig );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return ValueFactory.newString( value );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.STRING );
    }
}
