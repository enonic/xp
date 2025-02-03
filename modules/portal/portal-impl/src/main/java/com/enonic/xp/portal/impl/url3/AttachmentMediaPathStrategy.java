package com.enonic.xp.portal.impl.url3;

import com.enonic.xp.portal.url.PathStrategy;

public class AttachmentMediaPathStrategy
    implements PathStrategy
{
    private final String contentId;

    public AttachmentMediaPathStrategy( String contentId )
    {
        this.contentId = contentId;
    }

    @Override
    public String generatePath()
    {
        return "/media/attachment/" + this.contentId;
    }
}
