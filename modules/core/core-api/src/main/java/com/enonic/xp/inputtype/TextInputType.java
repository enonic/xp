package com.enonic.xp.inputtype;

import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.convert.ConvertException;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Input;

abstract class TextInputType
    extends InputTypeBase
{
    private final static Logger LOG = LoggerFactory.getLogger( LongType.class );

    protected TextInputType( final InputTypeName name )
    {
        super( name );
    }


    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateMaxLength( property, config );
    }

    private void validateMaxLength( final Property property, final InputTypeConfig config )
    {
        try
        {
            final Integer maxLength = config.getValue( "max-length", Integer.class );
            final Integer value = property.getString().length();

            if ( maxLength != null && value != null )
            {
                validateValue( property, value <= maxLength,
                               String.format( "Limit of %d character%s exceeded", maxLength, maxLength == 1 ? "" : 's' ) );
            }

        }
        catch ( ConvertException e )
        {
            LOG.warn( "Cannot convert 'max-length' config", e );
        }
    }
}
