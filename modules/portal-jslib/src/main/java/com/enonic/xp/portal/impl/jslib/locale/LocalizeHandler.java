package com.enonic.xp.portal.impl.jslib.locale;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.i18n.LocalizeParams;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;

@Component(immediate = true)
public final class LocalizeHandler
    implements CommandHandler
{
    private LocaleService localeService;

    private final List handlerParams = Lists.newArrayList( "key", "locale", "values" );

    @Override
    public String getName()
    {
        return "i18n.localize";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        final PortalRequest request = PortalRequestAccessor.get();
        final LocalizeParams params = new LocalizeParams( request ).setAsMap( toMap( req ) );

        final MessageBundle bundle = localeService.getBundle( params.getModuleKey(), params.getLocale() );
        return bundle.localize( params.getKey(), params.getParams() );
    }

    protected Multimap<String, String> toMap( final CommandRequest req )
    {
        final Multimap<String, String> map = HashMultimap.create();
        for ( final Map.Entry<String, Object> param : req.getParams().entrySet() )
        {
            final String key = param.getKey();

            if ( handlerParams.contains( key ) )
            {
                applyParam( map, "_" + key, param.getValue() );
            }
            else
            {
                applyParam( map, key, param.getValue() );
            }
        }

        return map;
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

    @Reference
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}
