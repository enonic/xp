package com.enonic.cms.web.rest.country;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.cms.core.country.Country;
import com.enonic.cms.core.country.Region;

public final class CountryModelTranslator
{
    public static CountryModel toModel( final Country entity )
    {
        final CountryModel model = new CountryModel();
        if ( entity != null )
        {
            model.setCode( entity.getCode().toString() );
            model.setEnglishName( entity.getEnglishName() );
            model.setLocalName( entity.getLocalName() );
            model.setRegionsEnglishName( entity.getRegionsEnglishName() );
            model.setRegionsLocalName( entity.getRegionsLocalName() );
            CallingCodeModel code = toCallingCodeModel( entity );
            List<CallingCodeModel> codes = new ArrayList<CallingCodeModel>( 1 );
            codes.add( code );
            model.setCallingCodes( codes );
            model.setRegions( toRegionsModel( entity ).getRegions() );
        }
        return model;
    }

    public static CountriesModel toModel( final Collection<Country> list )
    {
        final CountriesModel model = new CountriesModel();
        model.setTotal( list.size() );

        for ( final Country entity : list )
        {
            model.addCountry( toModel( entity ) );
        }
        return model;
    }

    public static RegionModel toRegionModel( final Region entity, final Country country )
    {
        final RegionModel model = new RegionModel();
        if ( entity != null )
        {
            model.setRegionCode( entity.getCode() );
            model.setCountryCode( country.getCode().toString() );
            model.setEnglishName( entity.getEnglishName() );
            model.setLocalName( entity.getLocalName() );
        }
        return model;
    }

    public static RegionsModel toRegionsModel( final Country country )
    {
        final RegionsModel model = new RegionsModel();
        Collection<Region> list = country.getRegions();
        model.setTotal( list.size() );

        for ( final Region entity : list )
        {
            model.addRegion( toRegionModel( entity, country ) );
        }
        return model;
    }

    static public CallingCodeModel toCallingCodeModel( Country country )
    {
        CallingCodeModel callingCode = new CallingCodeModel();
        callingCode.setCountryCode( country.getCode().toString() );
        callingCode.setCallingCode( "+" + country.getCallingCode() );
        callingCode.setEnglishName( country.getEnglishName() );
        callingCode.setLocalName( country.getLocalName() );

        return callingCode;
    }

    public static CallingCodesModel toCallingCodesModel( Collection<Country> countries )
    {
        final CallingCodesModel model = new CallingCodesModel();
        model.setTotal( countries.size() );

        for ( Country country : countries )
        {
            model.addCode( toCallingCodeModel( country ) );
        }
        return model;
    }
}
