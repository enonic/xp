package com.enonic.xp.lib.admin;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.admin.tool.AdminToolDescriptors;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.context.Context;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.lib.admin.mapper.AdminToolMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKeys;

public final class GetAdminToolsHandler
    implements ScriptBean
{
    private Supplier<AdminToolDescriptorService> adminToolDescriptorService;

    private Supplier<ApplicationService> applicationService;

    private Supplier<LocaleService> localeService;

    private Supplier<Context> context;

    private List<String> locales;

    public List<AdminToolMapper> execute()
    {
        final PrincipalKeys principalKeys = context.get().getAuthInfo().getPrincipals();
        final AdminToolDescriptors allowedTools = adminToolDescriptorService.get().getAllowedAdminToolDescriptors( principalKeys );

        return allowedTools.stream()
            .map( this::mapAdminTool )
            .collect( Collectors.toList() );
    }

    private AdminToolMapper mapAdminTool( final AdminToolDescriptor descriptor )
    {
        final Application application = applicationService.get().get( descriptor.getApplicationKey() );
        final String icon = adminToolDescriptorService.get().getIconByKey( descriptor.getKey() );
        
        final String localizedDisplayName = localizeProperty( descriptor.getDisplayNameI18nKey(), descriptor.getDisplayName(),
                                                               descriptor.getApplicationKey().toString() );
        final String localizedDescription =
            localizeProperty( descriptor.getDescriptionI18nKey(), descriptor.getDescription(), descriptor.getApplicationKey().toString() );

        return new AdminToolMapper( descriptor, application != null && application.isSystem(), localizedDisplayName,
                                     localizedDescription, icon );
    }

    private String localizeProperty( final String i18nKey, final String defaultValue, final String applicationKey )
    {
        if ( i18nKey == null || i18nKey.isEmpty() )
        {
            return defaultValue;
        }

        final Locale preferredLocale = resolvePreferredLocale( applicationKey );
        if ( preferredLocale == null )
        {
            return defaultValue;
        }

        try
        {
            final MessageBundle bundle =
                localeService.get().getBundle( com.enonic.xp.app.ApplicationKey.from( applicationKey ), preferredLocale );
            final String localized = bundle != null ? bundle.localize( i18nKey ) : null;
            return localized != null ? localized : defaultValue;
        }
        catch ( Exception e )
        {
            return defaultValue;
        }
    }

    private Locale resolvePreferredLocale( final String applicationKey )
    {
        if ( locales == null || locales.isEmpty() )
        {
            return Locale.ENGLISH;
        }

        final List<Locale> preferredLocales =
            locales.stream().map( Locale::forLanguageTag ).collect( Collectors.toList() );

        try
        {
            final Locale supportedLocale = localeService.get()
                .getSupportedLocale( preferredLocales, com.enonic.xp.app.ApplicationKey.from( applicationKey ) );
            return supportedLocale != null ? supportedLocale : Locale.ENGLISH;
        }
        catch ( Exception e )
        {
            return Locale.ENGLISH;
        }
    }

    public void setLocales( final List<String> locales )
    {
        this.locales = locales;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.adminToolDescriptorService = context.getService( AdminToolDescriptorService.class );
        this.applicationService = context.getService( ApplicationService.class );
        this.localeService = context.getService( LocaleService.class );
        this.context = context.getBinding( Context.class );
    }
}
