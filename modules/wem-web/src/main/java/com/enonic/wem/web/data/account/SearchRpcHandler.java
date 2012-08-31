package com.enonic.wem.web.data.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.json.JsonSerializable;
import com.enonic.wem.web.rest2.resource.account.AccountResource;
import com.enonic.wem.web.rpc.WebRpcContext;

@Component
public final class SearchRpcHandler
    extends AbstractDataRpcHandler
{
    @Autowired
    private AccountResource resource;

    public SearchRpcHandler()
    {
        super( "account_search" );
    }

    @Override
    public void handle( final WebRpcContext context )
        throws Exception
    {
        final int start = context.param( "start" ).asInteger( 0 );
        final int limit = context.param( "limit" ).asInteger( 10 );
        final String sort = context.param( "sort" ).asString( "" );
        final String dir = context.param( "dir" ).asString( "ASC" );
        final String query = context.param( "query" ).asString( "" );
        final String types = context.param( "types" ).asString( "user,group,role" );
        final String userStores = context.param( "userstores" ).asString( "" );
        final String organizations = context.param( "organizations" ).asString( "" );

        final JsonSerializable json = this.resource.search( start, limit, sort, dir, query, types, userStores, organizations );
        context.setResult( json );
    }
}
