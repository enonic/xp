package com.enonic.xp.portal.impl.view;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalContextAccessor;
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
        return "localizeFunction";
    }

    @Override
    public Object execute( final ViewFunctionParams params )
    {
        final ModuleKey module = PortalContextAccessor.get().getModule();
        String language = params.getRequiredValue( "language", String.class );
        String country = params.getRequiredValue( "country", String.class );
        final Locale locale = new Locale( language, country );
        return this.localeService.getBundle( module, locale );
    }

    @Reference
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}