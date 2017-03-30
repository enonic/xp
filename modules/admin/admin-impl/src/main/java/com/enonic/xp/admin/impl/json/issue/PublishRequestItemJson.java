package com.enonic.xp.admin.impl.json.issue;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.issue.PublishRequestItem;

@SuppressWarnings("unused")
public class PublishRequestItemJson
{
    private String id;

    private Boolean includeChildren;

    public PublishRequestItemJson() {

    }

    public PublishRequestItemJson( PublishRequestItem item )
    {
        this.id = item.getId().toString();
        this.includeChildren = item.getIncludeChildren();
    }

    public String getId()
    {
        return id;
    }

    public Boolean getIncludeChildren()
    {
        return includeChildren;
    }

    public PublishRequestItem toItem()
    {
        return PublishRequestItem.create().
            id( ContentId.from( this.id ) ).
            includeChildren( this.includeChildren ).
            build();

    }
}
