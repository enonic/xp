package com.enonic.xp.content;

import java.text.MessageFormat;

public class ValidationError
{
    private final String errorCode;

    private final String errorMessage;

    public ValidationError( final String errorCode, final String errorMessage, final Object... args )
    {
        this.errorCode = errorCode;
        this.errorMessage = args.length == 0 ? errorMessage : MessageFormat.format( errorMessage, args );
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public String getErrorCode()
    {
        return errorCode;
    }
}
