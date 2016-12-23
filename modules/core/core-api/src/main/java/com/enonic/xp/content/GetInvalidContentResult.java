package com.enonic.xp.content;

public class GetInvalidContentResult
{

    private ContentIds invalidContentIds;

    public GetInvalidContentResult( final ContentIds invalidContentIds )
    {
        this.invalidContentIds = invalidContentIds;
    }

    public ContentIds getInvalidContentIds()
    {
        return invalidContentIds;
    }

    public boolean isEmpty()
    {
        return this.invalidContentIds.isEmpty();
    }
}
