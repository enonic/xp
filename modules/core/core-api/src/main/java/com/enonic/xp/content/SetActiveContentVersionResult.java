package com.enonic.xp.content;

public class SetActiveContentVersionResult
{

    private final ContentVersionId contentVersionId;

    public SetActiveContentVersionResult( final ContentVersionId contentVersionId )
    {
        this.contentVersionId = contentVersionId;
    }

    public ContentVersionId getContentVersionId()
    {
        return contentVersionId;
    }
}
