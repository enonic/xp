package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

final class HtmlAreaType
    extends InputTypeBase
{
    public final static HtmlAreaType INSTANCE = new HtmlAreaType();

    private HtmlAreaType()
    {
        super( InputTypeName.HTML_AREA );
    }

    @Override
    public Value createDefaultValue( final Input input )
    {
        final String rootValue = input.getDefaultValue().getRootValue();
        if ( rootValue != null )
        {
            PropertySet propertySet = new PropertySet();
            propertySet.addString( "value", rootValue );

            return ValueFactory.newPropertySet( propertySet );
        }
        return super.createDefaultValue( input );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newPropertySet( value.asData() );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.PROPERTY_SET );
        validateType( property.getSet().getProperty( "value" ), ValueTypes.STRING );

        final Property references = property.getSet().getProperty( "references" );
        if ( references != null )
        {
            validateType( property.getSet().getProperty( "references" ), ValueTypes.PROPERTY_SET );
        }
    }
}
