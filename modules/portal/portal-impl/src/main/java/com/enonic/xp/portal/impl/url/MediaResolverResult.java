package com.enonic.xp.portal.impl.url;

import com.enonic.xp.content.Content;

final class MediaResolverResult
{
    private final Content content;

    private final String contentKey;

    MediaResolverResult( final Content content, final String contentKey )
    {
        this.content = content;
        this.contentKey = contentKey;
    }

    public Content getContent()
    {
        return content;
    }

    public String getContentKey()
    {
        return contentKey;
    }
}
