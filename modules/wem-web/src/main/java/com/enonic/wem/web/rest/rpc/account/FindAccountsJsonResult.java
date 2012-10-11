package com.enonic.wem.web.rest.rpc.account;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.account.query.AccountFacet;
import com.enonic.wem.api.account.query.AccountFacetEntry;
import com.enonic.wem.api.account.query.AccountQueryHits;

final class FindAccountsJsonResult
    extends AbstractAccountJsonResult
{
    private final AccountQueryHits hits;

    private final Accounts accounts;

    public FindAccountsJsonResult( final AccountQueryHits hits, final Accounts accounts )
    {
        this.hits = hits;
        this.accounts = accounts;
    }

    public FindAccountsJsonResult( final Accounts accounts )
    {
        this.hits = null;
        this.accounts = accounts;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        final ArrayNode accounts = json.putArray( "accounts" );
        for ( final Account account : this.accounts )
        {
            serializeAccount( accounts.addObject(), account );
        }

        if ( this.hits != null )
        {
            json.put( "total", this.hits.getTotalSize() );
            final ArrayNode facets = json.putArray( "facets" );
            for ( final AccountFacet facet : this.hits.getFacets() )
            {
                serializeFacet( facets.addObject(), facet );
            }
        }
        else
        {
            json.put( "total", this.accounts.getSize() );
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
