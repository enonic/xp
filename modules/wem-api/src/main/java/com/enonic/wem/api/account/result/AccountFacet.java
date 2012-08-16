package com.enonic.wem.api.account.result;

import java.util.List;

public interface AccountFacet
    extends Iterable<AccountFacet.Entry>
{
    public interface Entry
    {
        public String getTerm();

        public int getCount();
    }

    public String getName();

    public List<Entry> getEntries();
}
