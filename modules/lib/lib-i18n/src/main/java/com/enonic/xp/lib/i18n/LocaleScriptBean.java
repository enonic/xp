package com.enonic.xp.lib.i18n;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import com.google.common.base.Strings;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.site.Site;

import static java.util.stream.Collectors.toList;

public final class LocaleScriptBean
    implements ScriptBean
{
    private Supplier<LocaleService> localeService;

    private ApplicationKey application;

    private final static String NOT_TRANSLATED_MESSAGE = "NOT_TRANSLATED";

    public String localize( final String key, final List<String> locales, final ScriptValue values, String[] bundles )
    {
        if ( bundles == null || bundles.length == 0 )
        {
            bundles = new String[]{"i18n/phrases", "site/i18n/phrases"};
        }

        final String locale = getPreferredLocale( locales, bundles );
        final MessageBundle bundle = getMessageBundle( locale, bundles );

        if ( bundle == null )
        {
            return null;
        }

        final String localizedMessage = bundle.localize( key, toArray( values ) );

        return localizedMessage != null ? localizedMessage : NOT_TRANSLATED_MESSAGE;
    }

    public MapSerializable getPhrases( final List<String> locales, final String... bundleNames )
    {
        final String locale = getPreferredLocale( locales, bundleNames );
        return new MapMapper( getMessageBundle( locale, bundleNames ).asMap() );
    }

    public List<String> getSupportedLocales( final String... bundleNames )
    {
        final ApplicationKey applicationKey = getApplication();
        return this.localeService.get().getLocales( applicationKey, bundleNames ).stream().
            map( Locale::toLanguageTag ).
            sorted( String::compareTo ).
            collect( toList() );
    }

    private String getPreferredLocale( final List<String> localeTags, final String[] bundleNames )
    {
        if ( localeTags == null || localeTags.isEmpty() )
        {
            return null;
        }
        final ApplicationKey applicationKey = getApplication();
        final List<Locale> locales = localeTags.stream().map( Locale::forLanguageTag ).collect( toList() );
        final Locale preferredLocale = this.localeService.get().getSupportedLocale( locales, applicationKey, bundleNames );
        return preferredLocale == null ? null : preferredLocale.toLanguageTag();
    }

    private MessageBundle getMessageBundle( final String locale, final String... bundleNames )
    {
        final ApplicationKey applicationKey = getApplication();
        final Locale resolvedLocale = resolveLocale( locale );
        return this.localeService.get().getBundle( applicationKey, resolvedLocale, bundleNames );
    }

    private ApplicationKey getApplication()
    {
        if ( this.application != null )
        {
            return this.application;
        }
        else
        {
            final PortalRequest req = getRequest();
            return req != null ? req.getApplicationKey() : ApplicationKey.from( LocaleScriptBean.class );
        }
    }

    private Locale resolveLocale( final String locale )
    {
        return Strings.isNullOrEmpty( locale ) ? resolveLocaleFromSite() : Locale.forLanguageTag( locale );
    }

    private Locale resolveLocaleFromSite()
    {
        final PortalRequest request = getRequest();
        if ( request == null )
        {
            return null;
        }

        final Site site = request.getSite();

        if ( site != null )
        {
            return site.getLanguage();
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

    public void setApplication( final String application )
    {
        this.application = application == null ? null : ApplicationKey.from( application );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.localeService = context.getService( LocaleService.class );
    }

    private PortalRequest getRequest()
    {
        return PortalRequestAccessor.get();
    }
}
