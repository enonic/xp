package com.enonic.wem.web.rest.country;

import java.util.Collection;

import com.google.common.collect.Lists;

import com.enonic.cms.core.country.Country;
import com.enonic.cms.core.country.Region;

final class CountryModelTranslator
{
    private static CountryModel toModel( final Country entity )
    {
        final CountryModel model = new CountryModel();
        if (entity != null) {
            model.setCode( entity.getCode().toString() );
            model.setEnglishName( entity.getEnglishName() );
            model.setLocalName( entity.getLocalName() );
            model.setRegionsEnglishName( entity.getRegionsEnglishName() );
            model.setRegionsLocalName( entity.getRegionsLocalName() );
            model.setCallingCode( entity.getCallingCode() );
            model.setRegions( toModel( entity.getRegions() ) );
        }
        return model;
    }

    public static CountriesModel toModel( final Collection<Country> list )
    {
        final CountriesModel model = new CountriesModel();
        for ( final Country entity : list )
        {
            model.addCountry( toModel( entity ) );
        }
        return model;
    }

    private static RegionModel toModel( final Region entity )
    {
        final RegionModel model = new RegionModel();
        if (entity != null) {
            model.setCode( entity.getCode() );
            model.setEnglishName( entity.getEnglishName() );
            model.setLocalName( entity.getLocalName() );
        }
        return model;
    }

    private static Collection<RegionModel> toModel( final Collection<Region> list )
    {
        if (list.isEmpty()) {
            return null;
        }

        final Collection<RegionModel> model = Lists.newArrayList();
        for ( final Region entity : list )
        {
            model.add( toModel( entity ) );
        }
        return model;
    }
}
