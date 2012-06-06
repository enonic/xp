package com.enonic.cms.web.rest.locale;

import java.util.Collection;
import java.util.Locale;

final class LocaleModelHelper
{
    public static LocaleModel toModel(final Locale entity)
    {
        final LocaleModel model = new LocaleModel();
        model.setId( entity.toString() );
        model.setDisplayName( entity.getDisplayName() );
        return model;
    }

    public static LocalesModel toModel(final Collection<Locale> list)
    {
        final LocalesModel model = new LocalesModel();
        model.setTotal(list.size());

        for (final Locale entity : list) {
            model.addLocale(toModel(entity));
        }

        return model;
    }
}
