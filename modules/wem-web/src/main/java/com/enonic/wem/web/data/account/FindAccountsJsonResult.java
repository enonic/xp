package com.enonic.wem.web.data.account;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.query.AccountFacet;
import com.enonic.wem.api.account.query.AccountFacetEntry;
import com.enonic.wem.api.account.query.AccountResult;

final class FindAccountsJsonResult
    extends AbstractAccountJsonResult
{
    private final AccountResult result;

    public FindAccountsJsonResult( final AccountResult result )
    {
        this.result = result;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", this.result.getTotalSize() );

        final ArrayNode accounts = json.putArray( "accounts" );
        for ( final Account account : this.result )
        {
            serializeAccount( accounts.addObject(), account );
        }

        final ArrayNode facets = json.putArray( "facets" );
        for ( final AccountFacet facet : this.result.getFacets() )
        {
            serializeFacet( facets.addObject(), facet );
        }
    }

    private void serializeFacet( final ObjectNode json, final AccountFacet facet )
    {
        json.put( "name", facet.getName() );

        final ObjectNode terms = json.putObject( "terms" );
        for ( final AccountFacetEntry facetEntry : facet )
        {
            terms.put( facetEntry.getTerm(), facetEntry.getCount() );
        }
    }
}
