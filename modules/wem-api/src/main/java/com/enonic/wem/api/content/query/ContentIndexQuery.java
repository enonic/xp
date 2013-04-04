package com.enonic.wem.api.content.query;

import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.space.SpaceNames;

public class ContentIndexQuery
{
    private String fullTextSearchString;

    private boolean includeFacets = false;

    private String facets;

    private QualifiedContentTypeNames contentTypeNames;

    private SpaceNames spaceNames;

    public void setFullTextSearchString( final String fullTextSearchString )
    {
        this.fullTextSearchString = fullTextSearchString;
    }

    public String getFullTextSearchString()
    {
        return fullTextSearchString;
    }

    public boolean isIncludeFacets()
    {
        return includeFacets;
    }

    public void setIncludeFacets( final boolean includeFacets )
    {
        this.includeFacets = includeFacets;
    }

    public void setFacets( final String facets )
    {
        this.facets = facets;
    }

    public String getFacets()
    {
        return facets;
    }

    public QualifiedContentTypeNames getContentTypeNames()
    {
        return contentTypeNames;
    }

    public void setContentTypeNames( final QualifiedContentTypeNames contentTypeNames )
    {
        this.contentTypeNames = contentTypeNames;
    }

    public SpaceNames getSpaceNames()
    {
        return spaceNames;
    }

    public void setSpaceNames( final SpaceNames spaceNames )
    {
        this.spaceNames = spaceNames;
    }
}
