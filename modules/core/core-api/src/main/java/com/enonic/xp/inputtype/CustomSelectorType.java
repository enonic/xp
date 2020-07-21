package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

import static com.google.common.base.Strings.isNullOrEmpty;

final class CustomSelectorType
    extends InputTypeBase
{
    public static final CustomSelectorType INSTANCE = new CustomSelectorType();

    private CustomSelectorType()
    {
        super( InputTypeName.CUSTOM_SELECTOR );
    }

    @Override
    public Value createDefaultValue( final Input input )
    {
        final String defaultValue = input.getDefaultValue().getRootValue();
        if ( !isNullOrEmpty( defaultValue ) )
        {
            return ValueFactory.newString( defaultValue );
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

