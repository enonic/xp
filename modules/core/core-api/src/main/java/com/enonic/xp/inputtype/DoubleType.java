package com.enonic.xp.inputtype;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.convert.ConvertException;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

final class DoubleType
    extends NumberType
{
    public final static DoubleType INSTANCE = new DoubleType();

    private DoubleType()
    {
        super( InputTypeName.DOUBLE );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newDouble( value.asDouble() );
    }

    @Override
    public Value createDefaultValue( final Input input )
    {
        final String defaultValue = input.getDefaultValue().getRootValue();
        if ( StringUtils.isNotEmpty( defaultValue ) )
        {
            return ValueFactory.newDouble( Double.valueOf( defaultValue ) );
        }
        return super.createDefaultValue( input );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.DOUBLE );
    }
}
