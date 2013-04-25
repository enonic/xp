package com.enonic.wem.api.content.schema.content.form.inputtype;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.data.type.InvalidValueTypeException;
import com.enonic.wem.api.content.data.type.PropertyTypes;
import com.enonic.wem.api.content.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

import static com.enonic.wem.api.content.data.type.PropertyTool.newPropertyChecker;

public class Color
    extends BaseInputType
{
    public Color()
    {
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {
        final String stringValue = (String) property.getObject();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( property, this );
        }
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueTypeException, InvalidValueException
    {
        newPropertyChecker().pathRequired( "red" ).type( PropertyTypes.WHOLE_NUMBER ).range( 0, 255 ).check( property );
        newPropertyChecker().pathRequired( "green" ).type( PropertyTypes.WHOLE_NUMBER ).range( 0, 255 ).check( property );
        newPropertyChecker().pathRequired( "blue" ).type( PropertyTypes.WHOLE_NUMBER ).range( 0, 255 ).check( property );
    }

    @Override
    public Value newValue( final String value )
    {
        throw new UnsupportedOperationException();
    }
}
