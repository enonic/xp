package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.enonic.xp.admin.impl.json.content.ContentJson;

public class ContentTreeSelectorJson
{
    private ContentJson content;

    private Boolean selectable;

    private Boolean expandable;

    public ContentTreeSelectorJson( final ContentJson content, final Boolean selectable, final Boolean expandable )
    {
        this.content = content;
        this.selectable = selectable;
        this.expandable = expandable;
    }

    public ContentJson getContent()
    {
        return content;
    }

    public Boolean getSelectable()
    {
        return selectable;
    }

    public Boolean getExpandable()
    {
        return expandable;
    }
}
