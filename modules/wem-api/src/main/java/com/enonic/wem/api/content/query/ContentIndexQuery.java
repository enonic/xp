package com.enonic.wem.api.content.query;

public class ContentIndexQuery
{
    private String fullTextSearchString;

    private boolean includeFacets = false;

    private String facets;

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
}
