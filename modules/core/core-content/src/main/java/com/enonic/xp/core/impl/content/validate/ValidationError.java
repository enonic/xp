package com.enonic.xp.core.impl.content.validate;

import java.text.MessageFormat;

public class ValidationError
{

    private final String errorMessage;

    public ValidationError( final String errorMessage, final Object... args )
    {
        this.errorMessage = args.length == 0 ? errorMessage : MessageFormat.format( errorMessage, args );
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }
}
