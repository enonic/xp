package com.enonic.wem.web.rest2.resource.country;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.web.rest2.resource.AbstractResourceTest;

import com.enonic.cms.core.country.Country;
import com.enonic.cms.core.country.CountryCode;
import com.enonic.cms.core.country.CountryService;
import com.enonic.cms.core.country.Region;

public class CountryResourceTest
    extends AbstractResourceTest
{
    private CountryResource resource;

    private List<Country> countries;

    @Before
    public void setUp()
    {
        this.countries = Lists.newArrayList();

        final CountryService service = Mockito.mock( CountryService.class );
        Mockito.when( service.getCountries() ).thenReturn( this.countries );

        this.resource = new CountryResource();
        this.resource.setCountryService( service );
    }

    @Test
    public void testGetAll_empty()
        throws Exception
    {
        this.countries.clear();
        final CountryResult result = this.resource.getAll();
        assertJsonResult( "getAll_empty.json", result );
    }

    @Test
    public void testGetAll_list()
        throws Exception
    {
        this.countries.clear();

        final Country country = new Country( new CountryCode( "NO" ), "NORWAY", "NORGE", "47" );
        country.setRegionsEnglishName( "County" );
        country.setRegionsLocalName( "Fylke" );

        final Region region = new Region( "02", "Akershus", "Akershus" );
        country.addRegion( region );

        this.countries.add( country );

        final CountryResult result = this.resource.getAll();
        assertJsonResult( "getAll_list.json", result );
    }
}
