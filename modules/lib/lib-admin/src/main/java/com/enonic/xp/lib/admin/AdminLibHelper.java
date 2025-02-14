package com.enonic.xp.lib.admin;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;

public final class AdminLibHelper
    implements ScriptBean
{
    private static final String ADMIN_APP_NAME = "com.enonic.xp.app.main";

    private Supplier<AdminToolDescriptorService> adminToolDescriptorService;

    private Supplier<PortalRequest> requestSupplier;

    public AdminLibHelper()
    {
    }

    public String getBaseUri()
    {
        return this.adminToolDescriptorService.get().getHomeToolUri();
    }

    @Deprecated
    public String getAssetsUri()
    {
        return "/";
    }

    public String getHomeToolUri()
    {
        return this.adminToolDescriptorService.get().getHomeToolUri();
    }

    public String generateAdminToolUri( String application, String adminTool )
    {
        return this.adminToolDescriptorService.get().generateAdminToolUri( application, adminTool );
    }

    public String getHomeAppName()
    {
        return ADMIN_APP_NAME;
    }

    @Deprecated
    public String getLauncherToolUrl()
    {
        return generateAdminToolUri( ADMIN_APP_NAME, "launcher" );
    }

    public String getLocale()
    {
        final HttpServletRequest req = requestSupplier.get().getRawRequest();
        final Locale locale = req != null ? req.getLocale() : Locale.getDefault();
        return locale.getLanguage().toLowerCase();
    }

    public List<String> getLocales()
    {
        final HttpServletRequest req = requestSupplier.get().getRawRequest();
        final List<Locale> locales;
        if ( req != null )
        {
            locales = Collections.list( req.getLocales() );
        }
        else
        {
            locales = Collections.singletonList( Locale.getDefault() );
        }

        final List<String> localeList = locales.stream().map( Locale::toLanguageTag ).collect( Collectors.toList() );
        if ( localeList.isEmpty() )
        {
            return Collections.singletonList( Locale.getDefault().toLanguageTag() );
        }
        else
        {
            return localeList;
        }
    }

    public String getInstallation()
    {
        return ServerInfo.get().getName();
    }

    public String getVersion()
    {
        return VersionInfo.get().getVersion();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.adminToolDescriptorService = context.getService( AdminToolDescriptorService.class );
        this.requestSupplier = context.getBinding( PortalRequest.class );
    }
}
