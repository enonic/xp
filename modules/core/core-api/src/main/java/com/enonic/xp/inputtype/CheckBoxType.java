package com.enonic.xp.inputtype;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
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

        if ( !isNullOrEmpty( defaultValue ) )
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
