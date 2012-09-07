package com.enonic.wem.api.account.query;

public final class AccountFacetEntry
{
    private final String term;

    private final int count;

    public AccountFacetEntry( final String term, final int count )
    {
        this.term = term;
        this.count = count;
    }

    public String getTerm()
    {
        return this.term;
    }

    public int getCount()
    {
        return this.count;
    }
}
