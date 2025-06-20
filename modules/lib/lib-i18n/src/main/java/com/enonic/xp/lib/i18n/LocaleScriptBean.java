package com.enonic.xp.lib.i18n;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.script.serializer.MapSerializable;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;

public final class LocaleScriptBean
    implements ScriptBean
{
    private Supplier<LocaleService> localeService;

    private Supplier<PortalRequest> portalRequest;

    private ApplicationKey applicationKey;

    private static final String NOT_TRANSLATED_MESSAGE = "NOT_TRANSLATED";

    public String localize( final String key, final List<String> locales, final ScriptValue values, String[] bundles )
    {
        final String locale = getPreferredLocale( locales, bundles );
        final MessageBundle bundle = getMessageBundle( locale, bundles );

        if ( bundle == null )
        {
            return NOT_TRANSLATED_MESSAGE;
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
        final PortalRequest req = portalRequest.get();
        return req != null ? req.getApplicationKey() : applicationKey;
    }

    private Locale resolveLocale( final String locale )
    {
        return isNullOrEmpty( locale ) ? resolveLocaleFromSite() : Locale.forLanguageTag( locale );
    }

    private Locale resolveLocaleFromSite()
    {
        final PortalRequest request = portalRequest.get();
        if ( request == null )
        {
            return null;
        }

        final Content site = request.getSite();

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
            return value.getArray( String.class ).toArray( new String[0] );
        }

        return new String[0];
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.applicationKey = context.getApplicationKey();
        this.localeService = context.getService( LocaleService.class );
        this.portalRequest = context.getBinding( PortalRequest.class );
    }
}
