package com.enonic.wem.admin.rest.resource.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest2;
import com.enonic.wem.core.country.Country;
import com.enonic.wem.core.country.CountryCode;
import com.enonic.wem.core.country.CountryService;
import com.enonic.wem.core.country.Region;

public class CountryResourceTest
    extends AbstractResourceTest2
{
    @Override
    protected Object getResourceInstance()
    {
        final CountryService countryService = Mockito.mock( CountryService.class );

        final List<Country> countries = new ArrayList<>( 3 );

        final Country country = new Country( new CountryCode( "NO" ), "NORWAY", "NORGE", "47" );
        country.setRegionsEnglishName( "County" );
        country.setRegionsLocalName( "Fylke" );

        final Region region = new Region( "02", "Akershus", "Akershus" );
        country.addRegion( region );

        countries.add( country );

        Mockito.when( countryService.getCountries() ).thenReturn( countries );

        final CountryResource resource = new CountryResource();
        resource.setCountryService( countryService );
        return resource;
    }

    @Test
    public void testList()
        throws Exception
    {
        final String json = resource().path( "util/country" ).get( String.class );
        assertJson( "country_list.json", json );
    }
}
