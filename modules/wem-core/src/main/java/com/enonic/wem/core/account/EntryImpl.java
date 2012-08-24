package com.enonic.wem.core.account;

import com.enonic.wem.api.account.result.AccountFacet;

class EntryImpl
    implements AccountFacet.Entry
{
    private final String term;

    private final int count;

    EntryImpl( final String term, final int count )
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
