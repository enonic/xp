package com.enonic.xp.portal.impl.jslib.locale;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.i18n.LocalizeParams;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;

@Component(immediate = true)
public final class LocalizeHandler
    implements CommandHandler
{
    private LocaleService localeService;

    @Override
    public String getName()
    {
        return "i18n.localize";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        final PortalContext context = PortalContextAccessor.get();
        final LocalizeParams params = new LocalizeParams( context ).setAsMap( toMap( req ) );

        final MessageBundle bundle = localeService.getBundle( params.getModuleKey(), params.getLocale() );
        return bundle.localize( params.getKey(), params.getParams() );
    }

    protected Multimap<String, String> toMap( final CommandRequest req )
    {
        final Multimap<String, String> map = HashMultimap.create();
        for ( final Map.Entry<String, Object> param : req.getParams().entrySet() )
        {
            final String key = param.getKey();
            if ( key.equals( "params" ) )
            {
                applyParams( map, param.getValue() );
            }
            else
            {
                applyParam( map, "_" + key, param.getValue() );
            }
        }

        return map;
    }

    private void applyParams( final Multimap<String, String> params, final Map<?, ?> value )
    {
        for ( final Map.Entry<?, ?> entry : value.entrySet() )
        {
            final String key = entry.getKey().toString();
            applyParam( params, key, entry.getValue() );
        }
    }

    private void applyParam( final Multimap<String, String> params, final String key, final Object value )
    {
        if ( value instanceof Iterable )
        {
            applyParam( params, key, (Iterable) value );
        }
        else
        {
            params.put( key, value.toString() );
        }
    }

    private void applyParam( final Multimap<String, String> params, final String key, final Iterable values )
    {
        for ( final Object value : values )
        {
            params.put( key, value.toString() );
        }
    }

    private void applyParams( final Multimap<String, String> params, final Object value )
    {
        if ( value instanceof Map )
        {
            applyParams( params, (Map) value );
        }
    }

    @Reference
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}
