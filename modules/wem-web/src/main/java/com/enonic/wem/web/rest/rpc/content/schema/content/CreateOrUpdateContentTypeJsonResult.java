package com.enonic.wem.web.rest.rpc.content.schema.content;

import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.content.schema.content.UpdateContentTypeResult;
import com.enonic.wem.web.json.JsonResult;

final class CreateOrUpdateContentTypeJsonResult
    extends JsonResult
{
    private final boolean created;

    private final UpdateContentTypeResult updateResult;

    private CreateOrUpdateContentTypeJsonResult( final boolean created )
    {
        this.created = created;
        this.updateResult = null;
    }

    public CreateOrUpdateContentTypeJsonResult( final UpdateContentTypeResult result )
    {
        super( result == UpdateContentTypeResult.SUCCESS );
        this.updateResult = result;
        this.created = false;
    }

    public static CreateOrUpdateContentTypeJsonResult created()
    {
        return new CreateOrUpdateContentTypeJsonResult( true );
    }

    public static CreateOrUpdateContentTypeJsonResult updated()
    {
        return new CreateOrUpdateContentTypeJsonResult( false );
    }

    public static CreateOrUpdateContentTypeJsonResult from( final UpdateContentTypeResult result )
    {
        Preconditions.checkNotNull( result, "result cannot be null" );
        return new CreateOrUpdateContentTypeJsonResult( result );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "created", created );
        json.put( "updated", !created );
        if ( updateResult != null && updateResult != UpdateContentTypeResult.SUCCESS )
        {
            json.put( "failure", updateResult.toString() );
        }
    }

}
