package com.enonic.xp.portal.impl.url3;

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
