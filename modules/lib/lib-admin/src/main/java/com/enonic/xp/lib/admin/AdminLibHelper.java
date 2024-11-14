package com.enonic.xp.lib.admin;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.web.servlet.ServletRequestHolder;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

public final class AdminLibHelper
    implements ScriptBean
{
    private static final String ADMIN_APP_NAME = "com.enonic.xp.app.main";

    private static final String ADMIN_URI_PREFIX = "/admin";

    private static final String ADMIN_ASSETS_URI_PREFIX = "/admin/assets/";

    private final String version;

    private Supplier<AdminToolDescriptorService> adminToolDescriptorService;

    public AdminLibHelper()
    {
        this.version = generateVersion();
    }

    public String getBaseUri()
    {
        return ServletRequestUrlHelper.createUri( ServletRequestHolder.getRequest(), ADMIN_URI_PREFIX );
    }

    public String getAssetsUri()
    {
        return ServletRequestUrlHelper.createUri( ServletRequestHolder.getRequest(), ADMIN_ASSETS_URI_PREFIX + this.version );
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

    public String getLauncherToolUrl()
    {
        return generateAdminToolUri( ADMIN_APP_NAME, "launcher" );
    }

    public String getLocale()
    {
        final HttpServletRequest req = ServletRequestHolder.getRequest();
        final Locale locale = req != null ? req.getLocale() : Locale.getDefault();
        return locale.getLanguage().toLowerCase();
    }

    public List<String> getLocales()
    {
        final HttpServletRequest req = ServletRequestHolder.getRequest();
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

    private static String generateVersion()
    {
        final VersionInfo version = VersionInfo.get();
        if ( version.isSnapshot() )
        {
            return Long.toString( System.currentTimeMillis() );
        }
        else
        {
            return version.getVersion();
        }
    }

    public String getInstallation()
    {
        return ServerInfo.get().getName();
    }

    public String getVersion()
    {
        final VersionInfo version = VersionInfo.get();
        return version.getVersion();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.adminToolDescriptorService = context.getService( AdminToolDescriptorService.class );
    }
}
