package com.enonic.wem.api.export;

public class ExportError
{
    private final String message;

    public ExportError( final String message )
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return this.message;
    }
}
