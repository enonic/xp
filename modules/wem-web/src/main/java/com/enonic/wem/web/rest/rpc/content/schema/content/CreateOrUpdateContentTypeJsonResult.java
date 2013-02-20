package com.enonic.wem.web.rest.rpc.content.schema.content;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonResult;

final class CreateOrUpdateContentTypeJsonResult
    extends JsonResult
{
    private final boolean created;

    private CreateOrUpdateContentTypeJsonResult( final boolean created )
    {
        this.created = created;
    }

    public static CreateOrUpdateContentTypeJsonResult created()
    {
        return new CreateOrUpdateContentTypeJsonResult( true );
    }

    public static CreateOrUpdateContentTypeJsonResult updated()
    {
        return new CreateOrUpdateContentTypeJsonResult( false );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "created", created );
        json.put( "updated", !created );
    }

}
