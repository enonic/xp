package com.enonic.xp.lib.admin;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.web.servlet.ServletRequestHolder;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

import static java.util.stream.Collectors.toList;

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

    private static String rewriteUri( final String uri )
    {
        return ServletRequestUrlHelper.createUri( uri );
    }

    public String getBaseUri()
    {
        return rewriteUri( ADMIN_URI_PREFIX );
    }

    public String getAssetsUri()
    {
        return rewriteUri( ADMIN_ASSETS_URI_PREFIX + this.version );
    }

    public String getHomeToolUri()
    {
        return this.adminToolDescriptorService.get().getHomeToolUri();
    }

    public String generateAdminToolUri( String application, String adminTool )
    {
        return this.adminToolDescriptorService.get().generateAdminToolUri( application, adminTool );
    }

    public String getHomeAppName() {
        return ADMIN_APP_NAME;
    }

    public String getLauncherToolUrl() {
        return generateAdminToolUri(ADMIN_APP_NAME, "launcher");
    }

    public String getLocale()
    {
        final HttpServletRequest req = ServletRequestHolder.getRequest();
        final Locale locale = req != null ? req.getLocale() : Locale.getDefault();
        return resolveLanguage( locale.getLanguage().toLowerCase() );
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

        final List<String> localeList =
            locales.stream().map( ( l ) -> resolveLanguage( l.toLanguageTag().toLowerCase() ) ).collect( toList() );
        if ( localeList.isEmpty() )
        {
            return Collections.singletonList( resolveLanguage( Locale.getDefault().toLanguageTag().toLowerCase() ) );
        }
        else
        {
            return localeList;
        }
    }

    /**
     * This is a hack for now. We should resolve language in another way.
     */
    private String resolveLanguage( final String lang )
    {
        if ( lang.equals( "nn" ) )
        {
            return "no";
        }

        if ( lang.equals( "nb" ) )
        {
            return "no";
        }

        return lang;
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
