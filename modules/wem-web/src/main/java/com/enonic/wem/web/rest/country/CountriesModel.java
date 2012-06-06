package com.enonic.wem.web.rest.country;

import java.util.ArrayList;
import java.util.List;

public class CountriesModel
{
    private int total;

    private List<CountryModel> countries;

    public CountriesModel()
    {
        this.countries = new ArrayList<CountryModel>();
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal( int total )
    {
        this.total = total;
    }

    public List<CountryModel> getCountries()
    {
        return countries;
    }

    public void setCountries( List<CountryModel> countries )
    {
        this.countries = countries;
    }

    public void addCountry( CountryModel country )
    {
        this.countries.add( country );
    }
}
