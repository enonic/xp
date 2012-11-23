package com.enonic.wem.api.content.type;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.type.form.FormItemPath;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.Input;

public final class MissingRequiredValueValidationError
    extends DataValidationError
{

    public MissingRequiredValueValidationError( final Input input, final Data value )
    {
        super( input.getPath(), "Missing required value in input [{0}] of type [{1}]: {2}", input, input.getInputType(), value.getValue() );
    }

    public MissingRequiredValueValidationError( final FormItemSet formItemSet )
    {
        super( formItemSet.getPath(), "Missing required value in FormItemSet [{0}]", formItemSet );
    }
}
