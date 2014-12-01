package com.enonic.wem.api.schema.content.validator;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.form.Input;

public final class MissingRequiredValueValidationError
    extends DataValidationError
{
    public MissingRequiredValueValidationError( final Input input, final Property property )
    {
        super( input.getPath(), "Missing required value for input [{0}] of type [{1}]: {2}", input.getPath(), input.getInputType(),
               property.getObject() );
    }
}
