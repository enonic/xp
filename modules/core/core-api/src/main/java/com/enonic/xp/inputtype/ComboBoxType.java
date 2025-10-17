package com.enonic.xp.inputtype;

import java.util.Map;

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
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newString( value.asString() );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.STRING );

        final String valueAsString = property.getString();
        final boolean flag = ( valueAsString != null ) && config.getProperties( "option" )
            .stream()
            .map( InputTypeProperty::getValue )
            .filter( pv -> PropertyValue.Type.OBJECT == pv.getType() )
            .flatMap( pv -> pv.getProperties().stream() )
            .filter( e -> "value".equals( e.getKey() ) )
            .map( Map.Entry::getValue )
            .filter( pv -> PropertyValue.Type.STRING == pv.getType() )
            .map( PropertyValue::asString )
            .anyMatch( valueAsString::equals );

        validateValue( property, flag, "Value is not a valid option" );
    }
}
