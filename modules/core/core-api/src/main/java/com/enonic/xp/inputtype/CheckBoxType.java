package com.enonic.xp.inputtype;

import com.google.common.annotations.Beta;
import com.google.common.base.Strings;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

@Beta
final class CheckBoxType
    extends InputTypeBase
{
    public final static CheckBoxType INSTANCE = new CheckBoxType();

    private final static String VALID_VALUE = "checked";

    private CheckBoxType()
    {
        super( InputTypeName.CHECK_BOX );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newBoolean( value.asBoolean() );
    }

    @Override
    public Value createDefaultValue( final Input input )
    {
        final String defaultValue = input.getDefaultValue().getRootValue();

        if ( !Strings.nullToEmpty( defaultValue ).isEmpty() )
        {
            if ( VALID_VALUE.equals( defaultValue ) )
            {
                return ValueFactory.newBoolean( true );
            }
        }
        return super.createDefaultValue( input );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.BOOLEAN );
    }
}
