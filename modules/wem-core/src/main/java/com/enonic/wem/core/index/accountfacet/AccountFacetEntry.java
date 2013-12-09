package com.enonic.wem.core.index.accountfacet;

@Deprecated
public class AccountFacetEntry
{
    private final String term;

    private final int count;

    public AccountFacetEntry( String term, int count )
    {
        this.term = term;
        this.count = count;
    }

    public String getTerm()
    {
        return term;
    }

    public int getCount()
    {
        return count;
    }
}
