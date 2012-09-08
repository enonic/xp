package com.enonic.wem.web.json;

import org.codehaus.jackson.node.ObjectNode;

public final class JsonErrorResult
    extends JsonResult
{
    public JsonErrorResult( final String message )
    {
        super( false );
        error( message );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
    }
}
