package com.enonic.wem.web.rest.rpc.content;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonResult;

final class DeleteContentJsonResult
    extends JsonResult
{
    private final boolean success;

    private DeleteContentJsonResult( final boolean success )
    {
        this.success = success;
    }

    public static DeleteContentJsonResult successful()
    {
        return new DeleteContentJsonResult( true );
    }

    public static DeleteContentJsonResult unsuccessful()
    {
        return new DeleteContentJsonResult( false );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "deleted", success );
    }

}
