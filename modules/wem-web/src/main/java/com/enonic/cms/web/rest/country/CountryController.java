package com.enonic.cms.web.rest.country;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enonic.cms.core.country.Country;
import com.enonic.cms.core.country.CountryCode;
import com.enonic.cms.core.country.CountryService;

@Controller
@RequestMapping(value = "/misc", produces = MediaType.APPLICATION_JSON_VALUE)
public final class CountryController
{
    @Autowired
    private CountryService countryService;

    @RequestMapping(value = "country/list", method = RequestMethod.GET)
    @ResponseBody
    public CountriesModel getCountries( @RequestParam(value = "id", required = false) final String countryCode )
    {
        Collection<Country> list;
        if ( countryCode == null )
        {
            list = this.countryService.getCountries();
        }
        else
        {
            list = new ArrayList<Country>( 1 );
            Country country = this.countryService.getCountry( new CountryCode( countryCode ) );
            list.add( country );
        }
        return CountryModelTranslator.toModel( list );
    }

    @RequestMapping(value = "region/list", method = RequestMethod.GET)
    @ResponseBody
    public RegionsModel getRegions( @RequestParam(value = "countryCode", defaultValue = "") final String countryCode )
    {
        Country country = this.countryService.getCountry( new CountryCode( countryCode ) );
        RegionsModel model;
        if ( country != null )
        {
            model = CountryModelTranslator.toRegionsModel( country );
        }
        else
        {
            model = new RegionsModel();
        }
        return model;
    }

    @RequestMapping(value = "callingcodes/list", method = RequestMethod.GET)
    @ResponseBody
    public CallingCodesModel getAll()
    {
        return CountryModelTranslator.toCallingCodesModel( countryService.getCountries() );
    }
}
