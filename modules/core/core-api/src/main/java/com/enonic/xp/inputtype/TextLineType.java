package com.enonic.xp.inputtype;

import java.util.regex.PatternSyntaxException;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

import static com.google.common.base.Strings.isNullOrEmpty;

final class TextLineType
    extends TextInputTypeBase
{
    public static final TextLineType INSTANCE = new TextLineType();

    private TextLineType()
    {
        super( InputTypeName.TEXT_LINE );
    }

    private String regexp( final InputTypeConfig config )
    {
        return config.getProperty( "regexp" ).map( InputTypeProperty::getValue ).map( PropertyValue::asString ).orElse( "" );
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
        final String regexp = regexp( config ).trim();
        if ( !regexp.isEmpty() && !property.hasNullValue() )
        {
            try
            {
                final boolean matchesRegexp = property.getString().matches( regexp );
                validateValue( property, matchesRegexp, "Value does not match with regular expression" );
            }
            catch ( PatternSyntaxException e )
            {
                validateValue( property, false, "Invalid regexp '" + regexp + "' in " + this.getName() + " input type: " + e.getMessage() );
            }
        }

        super.validate( property, config );
    }
}
