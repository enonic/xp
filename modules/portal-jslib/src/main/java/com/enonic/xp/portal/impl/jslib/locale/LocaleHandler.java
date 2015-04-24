package com.enonic.xp.portal.impl.jslib.locale;

import java.util.List;

import org.apache.commons.lang.LocaleUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.i18n.LocaleParams;
import com.enonic.xp.portal.impl.jslib.base.BaseContextHandler;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;

@Component(immediate = true, service = CommandHandler.class)
public final class LocaleHandler
    extends BaseContextHandler
{

    private LocaleService localeService;

    @Override
    public final Object doExecute( final CommandRequest req )
    {
        final LocaleParams localeParams = createParams( req );
        final MessageBundle bundle =
            localeService.getBundle( PortalContextAccessor.get().getModule(), LocaleUtils.toLocale( localeParams.getLocale() ) );
        return bundle.localize( localeParams.getKey(), localeParams.getParams() );

    }

    private LocaleParams createParams( final CommandRequest req )
    {
        return new LocaleParams().
            setKey( req.param( "key" ).required().value( String.class ) ).
            setLocale( req.param( "locale" ).value( String.class ) ).
            setParams( req.param( "params" ).value( List.class ) );
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
