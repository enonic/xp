package com.enonic.wem.core.country;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CountryServiceImplTest
{
    private CountryServiceImpl service;

    @Before
    public void setUp()
        throws Exception
    {
        this.service = new CountryServiceImpl();
        this.service.afterPropertiesSet();
    }

    @Test
    public void testGetCountry()
    {
        Assert.assertNull( this.service.getCountry( new CountryCode( "XX" ) ) );

        Country country = this.service.getCountry( new CountryCode( "BB" ) );
        Assert.assertNotNull( country );
        Assert.assertEquals( "BB", country.getCode().toString() );
        Assert.assertEquals( "BARBADOS", country.getEnglishName() );
    }

    @Test
    public void testGetCountries()
    {
        Collection<Country> countries = this.service.getCountries();
        Assert.assertEquals( 246, countries.size() );

        Iterator<Country> it = countries.iterator();
        Assert.assertEquals( "AF", it.next().getCode().toString() );
        Assert.assertEquals( "AX", it.next().getCode().toString() );
    }
}
