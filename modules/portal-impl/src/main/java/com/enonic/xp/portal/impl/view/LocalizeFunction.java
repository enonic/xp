package com.enonic.xp.portal.impl.view;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.localize.LocalizeParams;
import com.enonic.xp.portal.view.ViewFunction;
import com.enonic.xp.portal.view.ViewFunctionParams;

@Component(immediate = true)
public final class LocalizeFunction
    implements ViewFunction
{
    private LocaleService localeService;

    @Override
    public String getName()
    {
        return "localize";
    }

    @Override
    public Object execute( final ViewFunctionParams params )
    {
        final LocalizeParams localizeParams = new LocalizeParams( PortalContextAccessor.get() ).setAsMap( params.getArgs() );

        final MessageBundle bundle = this.localeService.getBundle( localizeParams.getModuleKey(), localizeParams.getLocale() );

        return bundle.localize( localizeParams.getKey(), localizeParams.getParams() );
    }

    @Reference
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}