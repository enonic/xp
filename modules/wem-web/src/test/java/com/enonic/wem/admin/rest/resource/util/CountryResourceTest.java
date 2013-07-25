package com.enonic.wem.admin.rest.resource.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.TestUtil;
import com.enonic.wem.admin.rest.resource.util.model.CallingCodeJson;
import com.enonic.wem.admin.rest.resource.util.model.CountryJson;
import com.enonic.wem.admin.rest.resource.util.model.CountryListJson;
import com.enonic.wem.admin.rest.resource.util.model.RegionJson;
import com.enonic.wem.api.Client;
import com.enonic.wem.core.country.Country;
import com.enonic.wem.core.country.CountryCode;
import com.enonic.wem.core.country.CountryService;
import com.enonic.wem.core.country.Region;

import static org.junit.Assert.*;

public class CountryResourceTest
{

    private Client client;
    private CountryService countryService;

    @Before
    public void setup()
    {
        client = Mockito.mock( Client.class );
        countryService = Mockito.mock( CountryService.class );

        final List<Country> countries = new ArrayList<>( 3 );

        final Country country = new Country( new CountryCode( "NO" ), "NORWAY", "NORGE", "47" );
        country.setRegionsEnglishName( "County" );
        country.setRegionsLocalName( "Fylke" );

        final Region region = new Region( "02", "Akershus", "Akershus" );
        country.addRegion( region );

        countries.add( country );

        Mockito.when( countryService.getCountries() ).thenReturn( countries );
    }

    @Test
    public void testList()
        throws Exception
    {
        final CountryResource resource = new CountryResource();
        resource.setClient( client );
        resource.setCountryService( countryService );

        CountryListJson result = resource.list();

        Mockito.verify( countryService, Mockito.times( 1 ) ).getCountries();

        assertNotNull( result );
        assertEquals( 1, result.getTotal() );

        List<String> names = new ArrayList<>( 1 );
        for ( final CountryJson model : result.getCountries() )
        {
            names.add( model.getEnglishName() );
        }

        TestUtil.assertUnorderedArraysEquals( new String[]{"NORWAY"}, names.toArray() );

        RegionJson region = result.getCountries().get( 0 ).getRegions().get( 0 );
        assertEquals( "Akershus", region.getEnglishName() );

        CallingCodeJson code = result.getCountries().get( 0 ).getCallingCodes().get( 0 );
        assertEquals( "NORWAY", code.getEnglishName() );
    }
}
