package com.enonic.xp.portal.impl.jslib.locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.i18n.LocalizeParams;
import com.enonic.xp.portal.impl.jslib.base.BaseContextHandler;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;

@Component(immediate = true, service = CommandHandler.class)
public final class LocalizeHandler
    extends BaseContextHandler
{
    private LocaleService localeService;

    @Override
    public final Object doExecute( final CommandRequest req )
    {
        final LocalizeParams params = new LocalizeParams( PortalContextAccessor.get() ).setAsMap( toMap( req ) );

        final MessageBundle bundle = localeService.getBundle( PortalContextAccessor.get().getModule(), params.getLocale() );
        return bundle.localize( params.getKey(), params.getParams() );
    }

    @Override
    public String getName()
    {
        return "i18n.localize";
    }

    @Reference
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}
