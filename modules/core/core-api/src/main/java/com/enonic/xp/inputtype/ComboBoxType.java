package com.enonic.xp.inputtype;

import java.util.Objects;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

import static com.google.common.base.Strings.isNullOrEmpty;

final class ComboBoxType
    extends InputTypeBase
{
    public static final ComboBoxType INSTANCE = new ComboBoxType();

    private ComboBoxType()
    {
        super( InputTypeName.COMBO_BOX );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newString( value.asString() );
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
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.STRING );

        final String valueAsString = property.getString();
        final boolean flag = ( valueAsString != null ) && config.getProperties( "option" )
            .stream()
            .map( InputTypeProperty::getValue )
            .filter( ObjectPropertyValue.class::isInstance )
            .map( ObjectPropertyValue.class::cast )
            .map( ObjectPropertyValue::value )
            .map( m -> m.get( "value" ) )
            .filter( Objects::nonNull )
            .filter( StringPropertyValue.class::isInstance )
            .map( StringPropertyValue.class::cast )
            .map( StringPropertyValue::value )
            .anyMatch( valueAsString::equals );

        validateValue( property, flag, "Value is not a valid option" );
    }
}
