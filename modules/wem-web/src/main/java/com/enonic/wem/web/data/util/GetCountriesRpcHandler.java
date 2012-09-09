package com.enonic.wem.web.data.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.json.rpc.JsonRpcContext;

import com.enonic.cms.core.country.CountryService;

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

    @Autowired
    public void setCountryService( final CountryService countryService )
    {
        this.countryService = countryService;
    }
}
