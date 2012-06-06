package com.enonic.wem.web.rest.country;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.country.Country;
import com.enonic.cms.core.country.CountryService;

@Path("misc")
@Produces(MediaType.APPLICATION_JSON)
@Component
public final class CountryController
{
    @Autowired
    private CountryService countryService;

    @GET
    @Path("country")
    public CountriesModel getCountries()
    {
        final Collection<Country> list = this.countryService.getCountries();
        return CountryModelTranslator.toModel( list );
    }
}
