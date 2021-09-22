package com.enonic.xp.content;

public class ValidationError
{
    private final String errorCode;

    private final String message;

    private final I18nKey i18n;

    private final Object[] args;

    public ValidationError( final String errorCode, final I18nKey i18n, final Object... args )
    {
        this.errorCode = errorCode;
        this.message = null;
        this.i18n = i18n;
        this.args = args;
    }

    public ValidationError( final String errorCode, final String message, final I18nKey i18n, final Object... args )
    {
        this.errorCode = errorCode;
        this.message = null;
        this.i18n = i18n;
        this.args = args;
    }

    public ValidationError( final String errorCode, final String message )
    {
        this.errorCode = errorCode;
        this.message = message;
        this.i18n = null;
        this.args = null;
    }


    public String getMessage()
    {
        return message;
    }

    public String getErrorCode()
    {
        return errorCode;
    }

    public I18nKey getI18n()
    {
        return i18n;
    }

    public Object[] getArgs()
    {
        return args;
    }
}
