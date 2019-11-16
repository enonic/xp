package com.enonic.xp.inputtype;

import java.util.regex.PatternSyntaxException;

import com.google.common.base.Strings;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

final class TextLineType
    extends TextInputTypeBase
{
    public final static TextLineType INSTANCE = new TextLineType();

    private TextLineType()
    {
        super( InputTypeName.TEXT_LINE );
    }

    private String regexp( final InputTypeConfig config )
    {
        return config.getValue( "regexp", String.class, "" );
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
        if ( !Strings.nullToEmpty( defaultValue ).isEmpty() )
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
