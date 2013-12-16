package com.enonic.wem.portal.content;

import com.enonic.wem.api.content.Content;

public final class JsContextContent
{
    private final Content content;

    public JsContextContent( final Content content )
    {
        this.content = content;
    }

    public String getDisplayName()
    {
        return this.content.getDisplayName();
    }

    public String getPath()
    {
        return this.content.getPath().toString();
    }

    public String getName()
    {
        return this.content.getName().toString();
    }

}
