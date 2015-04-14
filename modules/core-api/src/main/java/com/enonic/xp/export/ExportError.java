package com.enonic.xp.export;

import com.google.common.annotations.Beta;

@Beta
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
