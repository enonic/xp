package com.enonic.wem.api.content.schema.type.validator;

import java.text.MessageFormat;

import javax.annotation.concurrent.Immutable;

import com.enonic.wem.api.content.schema.type.form.FormItemPath;

@Immutable
public class DataValidationError
{
    private final FormItemPath path;

    private final String errorMessage;

    DataValidationError( final FormItemPath path, final String errorMessage, final Object... args )
    {
        this.path = path;
        this.errorMessage = MessageFormat.format( errorMessage, args );
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
