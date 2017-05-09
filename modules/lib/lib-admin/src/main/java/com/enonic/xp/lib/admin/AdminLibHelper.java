package com.enonic.xp.lib.admin;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.web.servlet.ServletRequestHolder;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

public final class AdminLibHelper
{
    private static final String ADMIN_URI_PREFIX = "/admin";

    private static final String ADMIN_ASSETS_URI_PREFIX = "/admin/assets/";

    private final String version;

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

    public String getLocale()
    {
        final HttpServletRequest req = ServletRequestHolder.getRequest();
        return req != null ? req.getLocale().toString() : Locale.getDefault().toString();
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
}
