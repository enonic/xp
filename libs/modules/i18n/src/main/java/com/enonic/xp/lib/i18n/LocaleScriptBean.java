package com.enonic.xp.lib.i18n;

import java.util.List;
import java.util.Locale;

import com.google.common.base.Strings;

import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.script.ScriptValue;

public final class LocaleScriptBean
{
    private LocaleService localeService;

    public String localize( final String key, final String locale, final ScriptValue values )
    {
        final PortalRequest req = PortalRequestAccessor.get();
        final ModuleKey moduleKey = req.getModule();
        final Locale resolvedLocale = resolveLocale( req, locale );

        final MessageBundle bundle = this.localeService.getBundle( moduleKey, resolvedLocale );
        return bundle.localize( key, toArray( values ) );
    }

    private Locale resolveLocale( final PortalRequest req, final String locale )
    {
        return Strings.isNullOrEmpty( locale ) ? resolveLocaleFromSite( req ) : Locale.forLanguageTag( locale );
    }

    private Locale resolveLocaleFromSite( final PortalRequest req )
    {
        if ( req.getSite().getLanguage() != null )
        {
            return req.getSite().getLanguage();
        }

        return null;
    }

    private Object[] toArray( final ScriptValue value )
    {
        if ( ( value != null ) && value.isArray() )
        {
            return toArray( value.getArray( String.class ) );
        }

        return new String[0];
    }

    private String[] toArray( final List<String> value )
    {
        return value.toArray( new String[value.size()] );
    }

    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}
