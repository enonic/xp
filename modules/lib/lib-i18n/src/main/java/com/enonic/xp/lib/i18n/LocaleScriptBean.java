package com.enonic.xp.lib.i18n;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.site.Site;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class LocaleScriptBean
    implements ScriptBean
{
    private Supplier<LocaleService> localeService;

    private Supplier<PortalRequest> portalRequest;

    private ApplicationKey applicationKey;

    @Override
    public void initialize( final BeanContext context )
    {
        this.applicationKey = context.getApplicationKey();
        this.localeService = context.getService( LocaleService.class );
        this.portalRequest = context.getBinding( PortalRequest.class );
    }

    public String localize( final String key, final List<String> locales, final ScriptValue values, final List<String> bundleNames,
                            final String fallbackMessage )
    {
        final String[] bundleArray = bundleNames.toArray( String[]::new );
        final String locale = getPreferredLocale( locales, bundleArray );
        final MessageBundle messageBundle = getMessageBundle( locale, bundleArray );
        final String localizedMessage = messageBundle.localize( key, toArray( values ) );
        return Objects.requireNonNullElse( localizedMessage, fallbackMessage );
    }

    public MapSerializable getPhrases( final List<String> locales, final List<String> bundleNames )
    {
        final String[] bundleArray = bundleNames.toArray( String[]::new );
        final String locale = getPreferredLocale( locales, bundleArray );
        final MessageBundle messageBundle = getMessageBundle( locale, bundleArray );
        return new MapMapper( messageBundle.asMap() );
    }

    public List<String> getSupportedLocales( final List<String> bundleNames )
    {
        final String[] bundleArray = bundleNames.toArray( String[]::new );
        return this.localeService.get()
            .getLocales( this.applicationKey, bundleArray )
            .stream()
            .map( Locale::toLanguageTag )
            .sorted( String::compareTo )
            .toList();
    }

    private String getPreferredLocale( final List<String> localeTags, final String[] bundleNames )
    {
        if ( localeTags.isEmpty() )
        {
            return null;
        }
        final List<Locale> locales = localeTags.stream().map( Locale::forLanguageTag ).toList();
        final Locale preferredLocale = this.localeService.get().getSupportedLocale( locales, this.applicationKey, bundleNames );
        return preferredLocale == null ? null : preferredLocale.toLanguageTag();
    }

    private MessageBundle getMessageBundle( final String locale, final String[] bundleNames )
    {
        final Locale resolvedLocale = resolveLocale( locale );
        return this.localeService.get().getBundle( this.applicationKey, resolvedLocale, bundleNames );
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

        final Site site = request.getSite();

        if ( site != null )
        {
            return site.getLanguage();
        }

        return null;
    }

    private Object[] toArray( final ScriptValue value )
    {
        if ( value != null && value.isArray() )
        {
            return value.getArray( String.class ).toArray( String[]::new );
        }

        return new String[0];
    }
}
