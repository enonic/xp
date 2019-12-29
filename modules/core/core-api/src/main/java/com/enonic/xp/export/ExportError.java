package com.enonic.xp.export;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
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
