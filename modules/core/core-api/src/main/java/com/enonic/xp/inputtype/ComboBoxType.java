package com.enonic.xp.inputtype;

import java.util.Collection;
import java.util.Objects;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

final class ComboBoxType
    extends InputTypeBase
{
    public static final ComboBoxType INSTANCE = new ComboBoxType();

    private ComboBoxType()
    {
        super( InputTypeName.COMBO_BOX );
    }

    @Override
    public Value createValue( final Value value, final GenericValue config )
    {
        return ValueFactory.newString( value.asString() );
    }

    @Override
    public void validate( final Property property, final GenericValue config )
    {
        validateType( property, ValueTypes.STRING );

        final String valueAsString = property.getString();

        final boolean flag = valueAsString != null && config.optional( "option" )
            .map( GenericValue::asList )
            .stream()
            .flatMap( Collection::stream )
            .map( gv -> gv.property( "value" ) )
            .anyMatch( gv -> Objects.equals( valueAsString, gv.asString() ) );

        validateValue( property, flag, "Value is not a valid option" );
    }
}
