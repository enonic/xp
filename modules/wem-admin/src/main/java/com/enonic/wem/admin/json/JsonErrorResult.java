package com.enonic.wem.admin.json;

import java.text.MessageFormat;

import com.fasterxml.jackson.databind.node.ObjectNode;

public final class JsonErrorResult
    extends JsonResult
{
    public JsonErrorResult( final String message )
    {
        super( message );
    }

    public JsonErrorResult( final String message, final Object... args )
    {
        super( MessageFormat.format( message, args ) );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
    }
}
