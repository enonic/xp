package com.enonic.wem.web.rest.rpc.util;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;

import com.enonic.cms.core.country.Country;
import com.enonic.cms.core.country.CountryCode;
import com.enonic.cms.core.country.CountryService;
import com.enonic.cms.core.country.Region;

public class GetCountriesRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private List<Country> countries;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        this.countries = Lists.newArrayList();

        final CountryService service = Mockito.mock( CountryService.class );
        Mockito.when( service.getCountries() ).thenReturn( this.countries );

        final GetCountriesRpcHandler handler = new GetCountriesRpcHandler();
        handler.setCountryService( service );
        return handler;
    }

    @Test
    public void testRequest()
        throws Exception
    {
        final Country country = new Country( new CountryCode( "NO" ), "NORWAY", "NORGE", "47" );
        country.setRegionsEnglishName( "County" );
        country.setRegionsLocalName( "Fylke" );

        final Region region = new Region( "02", "Akershus", "Akershus" );
        country.addRegion( region );

        this.countries.add( country );

        testSuccess( "getCountries_result.json" );
    }
}
