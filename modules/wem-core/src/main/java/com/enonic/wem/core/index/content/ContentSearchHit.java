package com.enonic.wem.core.index.content;

import com.enonic.wem.api.content.ContentId;

public class ContentSearchHit
{
    private final ContentId contentId;

    private final float score;

    public ContentSearchHit( final ContentId contentId, final float score )
    {
        this.contentId = contentId;
        this.score = score;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public float getScore()
    {
        return score;
    }
}
