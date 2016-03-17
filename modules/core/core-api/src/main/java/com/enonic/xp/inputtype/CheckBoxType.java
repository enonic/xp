package com.enonic.xp.inputtype;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

@Beta
final class CheckBoxType
    extends InputTypeBase
{
    public final static CheckBoxType INSTANCE = new CheckBoxType();

    private static final String[] VALID_VALUES = {"true", "1", "checked"};

    private CheckBoxType()
    {
        super( InputTypeName.CHECK_BOX );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return ValueFactory.newBoolean( ValueTypes.BOOLEAN.convert( value ) );
    }

    @Override
    public Value createDefaultValue( final InputTypeConfig defaultValueConfig )
    {
        if ( defaultValueConfig.getProperty( "default" ) != null )
        {
            final String defaultValue = defaultValueConfig.getProperty( "default" ).getValue();
            return createValue( ArrayUtils.contains( VALID_VALUES, defaultValue ) ? "true" : "false", null );
        }
        return super.createDefaultValue( defaultValueConfig );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.BOOLEAN );
    }
}
