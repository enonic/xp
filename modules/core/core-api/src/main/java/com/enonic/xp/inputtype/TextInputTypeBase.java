package com.enonic.xp.inputtype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.convert.ConvertException;
import com.enonic.xp.data.Property;

abstract class TextInputTypeBase
    extends InputTypeBase
{
    private final static Logger LOG = LoggerFactory.getLogger( LongType.class );

    TextInputTypeBase( final InputTypeName name )
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
            final Integer maxLength = config.getValue( "maxLength", Integer.class );
            final String value = property.getString();

            if ( maxLength != null && value != null )
            {
                validateValue( property, value.length() <= maxLength,
                               String.format( "Limit of %d character%s exceeded", maxLength, maxLength == 1 ? "" : 's' ) );
            }
        }
        catch ( ConvertException e )
        {
            LOG.warn( "Cannot convert 'max-length' config", e );
        }
    }
}
