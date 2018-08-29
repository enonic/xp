package com.enonic.xp.admin.impl.json.content;

import java.util.Objects;

import com.enonic.xp.content.ContentListMetaData;


public class ContentListMetaDataJson
{
    final long totalHits;

    final long hits;

    public ContentListMetaDataJson( final ContentListMetaData contentListMetaData )
    {
        this( contentListMetaData.getTotalHits(), contentListMetaData.getHits() );
    }

    private ContentListMetaDataJson( final long totalHits, final long hits )
    {
        this.totalHits = totalHits;
        this.hits = hits;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public long getHits()
    {
        return hits;
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
        final ContentListMetaDataJson that = (ContentListMetaDataJson) o;
        return totalHits == that.totalHits && hits == that.hits;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( totalHits, hits );
    }
}
