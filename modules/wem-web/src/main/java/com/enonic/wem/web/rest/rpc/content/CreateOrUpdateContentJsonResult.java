package com.enonic.wem.web.rest.rpc.content;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.web.json.JsonResult;

final class CreateOrUpdateContentJsonResult
    extends JsonResult
{
    private final boolean created;

    private final ContentId contentId;

    private CreateOrUpdateContentJsonResult( final boolean created, final ContentId contentId )
    {
        this.created = created;
        this.contentId = contentId;
    }

    public static CreateOrUpdateContentJsonResult created( final ContentId contentId )
    {
        return new CreateOrUpdateContentJsonResult( true, contentId );
    }

    public static CreateOrUpdateContentJsonResult updated()
    {
        return new CreateOrUpdateContentJsonResult( false, null );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "created", created );
        json.put( "updated", !created );
        json.put( "contentId", contentId != null ? contentId.id() : null );
    }

}
