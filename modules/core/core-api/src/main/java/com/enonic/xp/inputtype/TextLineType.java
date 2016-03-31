package com.enonic.xp.inputtype;

import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

final class TextLineType
    extends InputTypeBase
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
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return ValueFactory.newString( value );
    }

    @Override
    public Value createDefaultValue( final InputTypeDefault defaultConfig )
    {
        final String defaultValue = defaultConfig.getRootValue();
        if ( StringUtils.isNotEmpty( defaultValue ) )
        {
            return ValueFactory.newString( defaultValue );
        }
        return super.createDefaultValue( defaultConfig );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.STRING );
        final String regexp = regexp( config ).trim();
        if ( regexp.isEmpty() || property.hasNullValue() )
        {
            return;
        }

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
}
