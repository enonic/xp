package com.enonic.wem.core.country;

import java.util.Collection;

public interface CountryService
{
    Collection<Country> getCountries();

    Country getCountry( CountryCode countryCode );
}
