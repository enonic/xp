package com.enonic.wem.web.rest.country;

import java.util.Collection;

import com.enonic.cms.core.country.Country;
import com.enonic.cms.core.country.Region;

public final class CountryModelTranslator
{
    public static CountryModel toModel( final Country entity )
    {
        final CountryModel model = new CountryModel();
        if (entity != null) {
            model.setCode( entity.getCode().toString() );
            model.setEnglishName( entity.getEnglishName() );
            model.setLocalName( entity.getLocalName() );
            model.setRegionsEnglishName( entity.getRegionsEnglishName() );
            model.setRegionsLocalName( entity.getRegionsLocalName() );
            model.setCallingCode( entity.getCallingCode() );
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

    public static RegionModel toModel( final Region entity )
    {
        final RegionModel model = new RegionModel();
        if (entity != null) {
            model.setCode( entity.getCode() );
            model.setEnglishName( entity.getEnglishName() );
            model.setLocalName( entity.getLocalName() );
        }
        return model;
    }

    public static RegionsModel toModel( final Collection<Region> list )
    {
        final RegionsModel model = new RegionsModel();
        model.setTotal( list.size() );

        for ( final Region entity : list )
        {
            model.addRegion( toModel( entity ) );
        }
        return model;
    }
}
