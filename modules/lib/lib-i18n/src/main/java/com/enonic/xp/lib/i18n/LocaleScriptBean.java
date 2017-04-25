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

public final class LocaleScriptBean
    implements ScriptBean
{
    private Supplier<LocaleService> localeService;

    public String localize( final String key, final String locale, final ScriptValue values )
    {
        return getMessageBundle( locale ).localize( key, toArray( values ) );
    }

    public MapSerializable getPhrases( final String locale )
    {
        return new MapMapper( getMessageBundle( locale ).asMap() );
    }

    private MessageBundle getMessageBundle( final String locale )
    {
        final ApplicationKey applicationKey = getRequest().getApplicationKey();
        final Locale resolvedLocale = resolveLocale( locale );
        return this.localeService.get().getBundle( applicationKey, resolvedLocale );
    }

    private Locale resolveLocale( final String locale )
    {
        return Strings.isNullOrEmpty( locale ) ? resolveLocaleFromSite() : Locale.forLanguageTag( locale );
    }

    private Locale resolveLocaleFromSite()
    {
        final PortalRequest request = getRequest();
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
