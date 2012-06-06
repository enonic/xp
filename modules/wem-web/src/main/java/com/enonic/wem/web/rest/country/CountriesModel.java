package com.enonic.wem.web.rest.country;

import java.util.ArrayList;
import java.util.List;

public class CountriesModel
{
    private List<CountryModel> countries;

    public CountriesModel()
    {
        this.countries = new ArrayList<CountryModel>();
    }

    public List<CountryModel> getCountries()
    {
        return countries;
    }

    public void setCountries( List<CountryModel> countries )
    {
        this.countries = countries;
    }

    public void addCountry(CountryModel country)
    {
        this.countries.add(country);
    }
}
