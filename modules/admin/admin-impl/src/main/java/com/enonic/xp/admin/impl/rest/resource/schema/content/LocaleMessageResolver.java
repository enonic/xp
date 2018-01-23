package com.enonic.xp.admin.impl.rest.resource.schema.content;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static java.util.stream.Collectors.toList;

public final class LocaleMessageResolver
{
    private LocaleService localeService;

    private ApplicationKey applicationKey;

    public LocaleMessageResolver( final LocaleService localeService )
    {
        this.localeService = localeService;
    }

    public LocaleMessageResolver( final LocaleService localeService, final ApplicationKey applicationKey )
    {
        this( localeService );
        this.applicationKey = applicationKey;
    }

    public String localizeMessage( final String key, final String defaultValue )
    {
        final MessageBundle bundle = this.localeService.getBundle( applicationKey, getLocale() );

        if ( bundle == null )
        {
            return defaultValue;
        }
        final String localizedValue = bundle.localize( key );
        return localizedValue != null ? localizedValue : defaultValue;
    }

    private Locale getLocale()
    {
        final HttpServletRequest req = ServletRequestHolder.getRequest();
        if ( req == null )
        {
            return null;
        }

        final List<Locale> preferredLocales = Collections.list( req.getLocales() ).
            stream().
            map( this::resolveLanguage ).
            collect( toList() );

        return localeService.getSupportedLocale( preferredLocales, applicationKey );
    }

    private Locale resolveLanguage( final Locale locale )
    {
        final String lang = locale.getLanguage();
        if ( lang.equals( "nn" ) || lang.equals( "nb" ) )
        {
            return new Locale( "no" );
        }
        return locale;
    }

}
