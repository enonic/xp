package com.enonic.wem.admin.rest.resource.util.model;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.core.country.Country;

public class CountryListJson
{
    private final ImmutableList<CountryJson> list;

    public CountryListJson( final Collection<Country> countries )
    {
        final ImmutableList.Builder<CountryJson> builder = ImmutableList.builder();
        for ( final Country country : countries )
        {
            builder.add( new CountryJson( country ) );
        }

        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<CountryJson> getCountries()
    {
        return this.list;
    }
}
