package com.enonic.xp.admin.impl.json.content;

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
}
