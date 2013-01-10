package com.enonic.wem.api.content.type.validator;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.type.form.Input;

public final class MissingRequiredValueValidationError
    extends DataValidationError
{
    public MissingRequiredValueValidationError( final Input input, final Data data )
    {
        super( input.getPath(), "Missing required value for input [{0}] of type [{1}]: {2}", input, input.getInputType(),
               data.getObject() );
    }
}
