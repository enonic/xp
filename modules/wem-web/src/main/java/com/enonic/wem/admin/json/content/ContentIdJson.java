package com.enonic.wem.admin.json.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;

public class ContentIdJson
{
    private String id;

    public ContentIdJson( Content content )
    {
        if ( content != null && content.getId() != null )
        {
            this.id = content.getId().toString();
        }
    }

    public ContentIdJson( ContentId contentId )
    {
        if ( contentId != null )
        {
            this.id = contentId.toString();
        }
    }

    public ContentIdJson( final String id )
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
}
