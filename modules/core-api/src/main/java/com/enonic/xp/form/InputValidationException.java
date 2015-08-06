package com.enonic.xp.form;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.form.inputtype.InputType;

@Beta
public class InputValidationException
    extends RuntimeException
{
    private final Property property;

    public InputValidationException( final Property property, final InputType inputType )
    {
        super( buildMessage( property, inputType ) );
        this.property = property;
    }

    public Property getProperty()
    {
        return property;
    }

    private static String buildMessage( final Property property, final InputType inputType )
    {
        return "Validation error on [" + property.getPath() + "] of type [" + inputType + "] , value was: " +
            property.getBoolean();
    }
}
