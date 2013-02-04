package com.enonic.wem.web.rest.rpc.content;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.web.json.JsonResult;

final class CreateOrUpdateContentJsonResult
    extends JsonResult
{
    private final boolean created;

    private final ContentId contentId;

    private final ContentPath contentPath;

    private CreateOrUpdateContentJsonResult( final boolean created, final ContentId contentId, final ContentPath contentPath )
    {
        this.created = created;
        this.contentId = contentId;
        this.contentPath = contentPath;
    }

    public static CreateOrUpdateContentJsonResult created( final ContentId contentId, final ContentPath contentPath )
    {
        return new CreateOrUpdateContentJsonResult( true, contentId, contentPath );
    }

    public static CreateOrUpdateContentJsonResult updated()
    {
        return new CreateOrUpdateContentJsonResult( false, null, null );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "created", created );
        json.put( "updated", !created );
        if ( contentId != null )
        {
            json.put( "contentId", contentId.id() );
        }
        if ( contentPath != null )
        {
            json.put( "contentPath", contentPath.toString() );
        }
    }

}
