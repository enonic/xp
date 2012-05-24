package com.enonic.wem.web.rest.country;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.country.Country;
import com.enonic.cms.core.country.CountryCode;
import com.enonic.cms.core.country.CountryService;
import com.enonic.cms.core.country.Region;

@Path("misc")
@Produces(MediaType.APPLICATION_JSON)
@Component
public final class CountryController
{
    @Autowired
    private CountryService countryService;

    @GET
    @Path("country/list")
    public CountriesModel getCountries()
    {
        Collection<Country> list = this.countryService.getCountries();
        return CountryModelTranslator.toModel( list );
    }

    @GET
    @Path("callingcodes/list")
    public CallingCodesModel getCallingCodes()
    {
        List<CallingCodeModel> list = new ArrayList<CallingCodeModel>();
        for ( Country c : countryService.getCountries() )
        {
            list.add( CallingCodeModelTranslator.toModel( c ) );
        }
        CallingCodesModel codes = new CallingCodesModel();
        codes.setCodes( list );
        codes.setTotal( list.size() );
        return codes;
    }

    @GET
    @Path("region/list")
    public RegionsModel getRegions( @QueryParam("countryCode") final String countryCode )
    {
        Country country = this.countryService.getCountry( new CountryCode( countryCode ) );
        RegionsModel model;
        if ( country != null )
        {
            Collection<Region> list = country.getRegions();
            model = CountryModelTranslator.toModel( list );
        }
        else
        {
            model = new RegionsModel();
        }
        return model;
    }
}
