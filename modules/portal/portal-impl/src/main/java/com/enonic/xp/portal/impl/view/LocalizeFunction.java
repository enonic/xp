package com.enonic.xp.portal.impl.view;

import java.text.MessageFormat;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.view.ViewFunction;
import com.enonic.xp.portal.view.ViewFunctionParams;

@Component(immediate = true)
public final class LocalizeFunction
    implements ViewFunction
{
    private LocaleService localeService;

    final static String NO_MATCHING_BUNDLE = "no localization bundle found in application ''{0}''";

    private final static String NOT_TRANSLATED_MESSAGE = "NOT_TRANSLATED";

    @Override
    public String getName()
    {
        return "i18n.localize";
    }

    @Override
    public Object execute( final ViewFunctionParams params )
    {
        final LocalizeParams localizeParams = new LocalizeParams( PortalRequestAccessor.get() ).setAsMap( params.getArgs() );

        final MessageBundle bundle = this.localeService.getBundle( localizeParams.getApplicationKey(), localizeParams.getLocale() );

        if ( bundle == null )
        {
            return MessageFormat.format( NO_MATCHING_BUNDLE, localizeParams.getApplicationKey() );
        }
        final String localizedMessage = bundle.localize( localizeParams.getKey(), localizeParams.getParams() );

        return localizedMessage != null ? localizedMessage : NOT_TRANSLATED_MESSAGE;
    }

    @Reference
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}