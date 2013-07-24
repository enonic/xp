package com.enonic.wem.admin.rest.resource.util;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.util.model.CountryListJson;
import com.enonic.wem.core.country.Country;
import com.enonic.wem.core.country.CountryService;

@Path("util/country")
@Produces(MediaType.APPLICATION_JSON)
public class CountryResource
    extends AbstractResource
{
    private CountryService countryService;

    @GET
    public CountryListJson list()
    {
        final Collection<Country> countries = this.countryService.getCountries();
        final CountryListJson result = new CountryListJson( countries  );

        return result;
    }

    @Inject
    public void setCountryService( final CountryService countryService )
    {
        this.countryService = countryService;
    }
}
