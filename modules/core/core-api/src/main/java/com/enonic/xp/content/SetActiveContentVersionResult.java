package com.enonic.xp.content;

public class SetActiveContentVersionResult
{

    private final ContentId contentId;

    private final ContentVersionId contentVersionId;

    public SetActiveContentVersionResult( final ContentId contentId, final ContentVersionId contentVersionId )
    {
        this.contentId = contentId;
        this.contentVersionId = contentVersionId;
    }

    public ContentVersionId getContentVersionId()
    {
        return contentVersionId;
    }

    public ContentId getContentId()
    {
        return contentId;
    }
}
