package com.enonic.wem.portal.exception.renderer;

final class StatusErrorInfo
{
    private String title;

    private int statusCode;

    private String description;

    private SourceInfo source;

    private CallStackInfo callStack;

    private CauseInfo cause;

    public String getTitle()
    {
        return this.title;
    }

    public int getStatusCode()
    {
        return this.statusCode;
    }

    public String getDescription()
    {
        return this.description;
    }

    public SourceInfo getSource()
    {
        return this.source;
    }

    public CallStackInfo getCallStack()
    {
        return this.callStack;
    }

    public CauseInfo getCause()
    {
        return this.cause;
    }

    public StatusErrorInfo title( final String title )
    {
        this.title = title;
        return this;
    }

    public StatusErrorInfo statusCode( final int statusCode )
    {
        this.statusCode = statusCode;
        return this;
    }

    public StatusErrorInfo description( final String description )
    {
        this.description = description;
        return this;
    }

    public StatusErrorInfo source( final SourceInfo source )
    {
        this.source = source;
        return this;
    }

    public StatusErrorInfo callStack( final CallStackInfo callStack )
    {
        this.callStack = callStack;
        return this;
    }

    public StatusErrorInfo cause( final CauseInfo cause )
    {
        this.cause = cause;
        return this;
    }
}
