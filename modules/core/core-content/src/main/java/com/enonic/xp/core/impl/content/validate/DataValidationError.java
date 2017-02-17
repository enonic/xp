package com.enonic.xp.core.impl.content.validate;

import com.enonic.xp.form.FormItemPath;

public class DataValidationError
    extends ValidationError
{
    private final FormItemPath path;


    DataValidationError( final FormItemPath path, final String errorMessage, final Object... args )
    {
        super( errorMessage, args );
        this.path = path;
    }

    public FormItemPath getPath()
    {
        return path;
    }

}
