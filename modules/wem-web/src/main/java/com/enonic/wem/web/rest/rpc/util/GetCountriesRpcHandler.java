package com.enonic.wem.web.rest.rpc.util;

import javax.inject.Inject;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.country.CountryService;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.json.rpc.JsonRpcContext;

@Component
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
