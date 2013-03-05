package com.enonic.wem.api.content.query;

public class ContentIndexQuery
{
    private String fullTextSearchString;

    public void setFullTextSearchString( final String fullTextSearchString )
    {
        this.fullTextSearchString = fullTextSearchString;
    }

    public String getFullTextSearchString()
    {
        return fullTextSearchString;
    }


}
