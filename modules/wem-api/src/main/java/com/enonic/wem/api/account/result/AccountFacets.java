package com.enonic.wem.api.account.result;

import java.util.List;
import java.util.Map;

public interface AccountFacets
    extends Iterable<AccountFacet>
{
    public AccountFacet getFacet( String name );

    public List<AccountFacet> getFacets();

    public Map<String, AccountFacet> asMap();
}
