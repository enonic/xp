package com.enonic.xp.portal.impl.view;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.module.ModuleKey;
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
        final ModuleKey module = params.getContext().getModule();
        final Locale locale = params.getContext().getSite().getLanguage();
        final String key = params.getRequiredValue( "key", String.class );
        return this.localeService.getBundle( module, locale ).localize( key );
    }

    @Reference
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}