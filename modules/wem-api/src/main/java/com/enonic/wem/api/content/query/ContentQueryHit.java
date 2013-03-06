package com.enonic.wem.api.content.query;

import com.enonic.wem.api.content.ContentId;

public class ContentQueryHit
{
    private final ContentId contentId;

    private final float score;

    public ContentQueryHit( final float score, final ContentId contentId )
    {
        this.score = score;
        this.contentId = contentId;
    }


    public float getScore()
    {
        return score;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final ContentQueryHit that = (ContentQueryHit) o;

        if ( Float.compare( that.score, score ) != 0 )
        {
            return false;
        }
        if ( contentId != null ? !contentId.equals( that.contentId ) : that.contentId != null )
        {
            return false;
        }

        return true;
    }

}
