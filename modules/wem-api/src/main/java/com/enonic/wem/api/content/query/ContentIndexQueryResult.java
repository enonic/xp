package com.enonic.wem.api.content.query;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.query.FacetsResultSet;

public class ContentIndexQueryResult
{
    private final int totalSize;

    private Set<ContentQueryHit> contentQueryHits = Sets.newLinkedHashSet();

    private FacetsResultSet facetsResultSet;

    public ContentIndexQueryResult( final int totalSize )
    {
        this.totalSize = totalSize;
    }

    public int getTotalSize()
    {
        return totalSize;
    }

    public void addContentHit( final ContentId contentId, final float score )
    {
        this.contentQueryHits.add( new ContentQueryHit( score, contentId ) );
    }

    public Set<ContentId> getContentIds()
    {
        final LinkedHashSet<ContentId> contentIds = Sets.newLinkedHashSet();

        for ( ContentQueryHit hit : contentQueryHits )
        {
            contentIds.add( hit.getContentId() );
        }

        return contentIds;
    }

    public Set<ContentQueryHit> getContentQueryHits()
    {
        return contentQueryHits;
    }

    public void setFacetsResultSet( final FacetsResultSet facetsResultSet )
    {
        this.facetsResultSet = facetsResultSet;
    }

    public FacetsResultSet getFacetsResultSet()
    {
        return facetsResultSet;
    }
}
