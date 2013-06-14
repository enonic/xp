package com.enonic.wem.core.country;

import java.util.Collection;

import com.google.inject.ImplementedBy;

@ImplementedBy( CountryServiceImpl.class )
public interface CountryService
{
    Collection<Country> getCountries();

    Country getCountry( CountryCode countryCode );
}
