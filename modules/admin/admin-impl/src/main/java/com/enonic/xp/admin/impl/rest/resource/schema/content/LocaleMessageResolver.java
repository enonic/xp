package com.enonic.xp.admin.impl.rest.resource.schema.content;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.web.servlet.ServletRequestHolder;

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

    public void setApplicationKey( final ApplicationKey applicationKey )
    {
        this.applicationKey = applicationKey;
    }

    public String localizeMessage( final String key, final String defaultValue )
    {
        return localizeMessage( this.applicationKey, key, defaultValue );
    }

    public String localizeMessage( final ApplicationKey applicationKey, final String key, final String defaultValue )
    {
        final MessageBundle bundle = this.localeService.getBundle( applicationKey, new Locale( getLocale() ) );

        if ( bundle == null )
        {
            return defaultValue;
        }
        final String localizedValue = bundle.localize( key );
        return localizedValue != null ? localizedValue : defaultValue;
    }

    private String getLocale()
    {
        final HttpServletRequest req = ServletRequestHolder.getRequest();
        final Locale locale = req != null ? req.getLocale() : Locale.getDefault();
        return resolveLanguage( locale.getLanguage().toLowerCase() );
    }

    private String resolveLanguage( final String lang )
    {
        if ( lang.equals( "nn" ) )
        {
            return "no";
        }

        if ( lang.equals( "nb" ) )
        {
            return "no";
        }

        return lang;
    }

}
