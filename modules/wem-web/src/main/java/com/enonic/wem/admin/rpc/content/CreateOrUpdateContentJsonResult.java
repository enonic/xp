package com.enonic.wem.admin.rpc.content;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.api.command.content.UpdateContentResult;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;

final class CreateOrUpdateContentJsonResult
    extends JsonResult
{
    private final boolean created;

    private final ContentId contentId;

    private final ContentPath contentPath;

    private final UpdateContentResult updateResult;

    private CreateOrUpdateContentJsonResult( final boolean created, final ContentId contentId, final ContentPath contentPath )
    {
        this.created = created;
        this.contentId = contentId;
        this.contentPath = contentPath;
        this.updateResult = null;
    }

    private CreateOrUpdateContentJsonResult( final UpdateContentResult result )
    {
        created = false;
        contentId = null;
        contentPath = null;
        updateResult = result;
    }

    public static CreateOrUpdateContentJsonResult created( final ContentId contentId, final ContentPath contentPath )
    {
        return new CreateOrUpdateContentJsonResult( true, contentId, contentPath );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "created", created );
        json.put( "updated", !created );
        if ( contentId != null )
        {
            json.put( "contentId", contentId.toString() );
        }
        if ( contentPath != null )
        {
            json.put( "contentPath", contentPath.toString() );
        }
        if ( updateResult != null && updateResult.getType() != UpdateContentResult.Type.SUCCESS )
        {
            json.put( "failure", updateResult.toString() );
        }
    }

    public static CreateOrUpdateContentJsonResult from( final UpdateContentResult result )
    {
        return new CreateOrUpdateContentJsonResult( result );
    }
}
