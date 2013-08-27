package com.enonic.wem.api.facet;

public class TermsFacetEntry
{
    private String term;

    private String displayName;

    private Integer count;

    protected TermsFacetEntry( final String term, final Integer count )
    {
        this.term = term;
        this.count = count;
        this.displayName = term;
    }

    protected TermsFacetEntry( final String term, final String displayName, final Integer count )
    {
        this.term = term;
        this.displayName = displayName;
        this.count = count;
    }

    public String getTerm()
    {
        return term;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public Integer getCount()
    {
        return count;
    }

}
