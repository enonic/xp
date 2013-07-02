package com.enonic.wem.admin.rpc.util;

import javax.inject.Inject;

import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.core.country.CountryService;


public final class GetCountriesRpcHandler
    extends AbstractDataRpcHandler
{
    private CountryService countryService;

    public GetCountriesRpcHandler()
    {
        super( "util_getCountries" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final CountryJsonResult result = new CountryJsonResult( this.countryService.getCountries() );
        context.setResult( result );
    }

    @Inject
    public void setCountryService( final CountryService countryService )
    {
        this.countryService = countryService;
    }
}
