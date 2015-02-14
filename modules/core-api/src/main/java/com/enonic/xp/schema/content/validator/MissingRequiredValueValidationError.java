package com.enonic.xp.schema.content.validator;

import com.enonic.xp.data.Property;
import com.enonic.xp.form.Input;

public final class MissingRequiredValueValidationError
    extends DataValidationError
{
    public MissingRequiredValueValidationError( final Input input, final Property property )
    {
        super( input.getPath(), "Missing required value for input [{0}] of type [{1}]: {2}", input.getPath(), input.getInputType(),
               property.getObject() );
    }
}
