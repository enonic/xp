package com.enonic.wem.web.rest.rpc.content.type;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonResult;

final class DeleteContentTypeJsonResult
    extends JsonResult
{
    private final int deleted;


    public DeleteContentTypeJsonResult( final Integer deleted )
    {
        this.deleted = deleted != null ? deleted : 0;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "deleted", deleted );
    }
}
