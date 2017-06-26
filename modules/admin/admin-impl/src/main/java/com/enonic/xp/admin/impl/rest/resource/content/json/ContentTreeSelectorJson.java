package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.enonic.xp.admin.impl.json.content.ContentJson;

public class ContentTreeSelectorJson
{
    private Boolean expand;

    private ContentJson content;

    public ContentTreeSelectorJson( final ContentJson content, final Boolean expand )
    {
        this.content = content;
        this.expand = expand;
    }

    public ContentJson getContent()
    {
        return content;
    }

    public Boolean getExpand()
    {
        return expand;
    }
}
