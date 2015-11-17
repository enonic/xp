package com.enonic.xp.core.impl.content.validate;

import java.text.MessageFormat;

import com.enonic.xp.form.FormItemPath;

public class DataValidationError
{
    private final FormItemPath path;

    private final String errorMessage;

    DataValidationError( final FormItemPath path, final String errorMessage, final Object... args )
    {
        this.path = path;
        this.errorMessage = args.length == 0 ? errorMessage : MessageFormat.format( errorMessage, args );
    }

    public FormItemPath getPath()
    {
        return path;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }
}
