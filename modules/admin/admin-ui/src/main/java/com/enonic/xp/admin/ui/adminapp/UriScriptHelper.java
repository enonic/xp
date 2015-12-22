package com.enonic.xp.admin.ui.adminapp;


import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

public class UriScriptHelper
{
    public static final String ADMIN_ASSETS_URI_PREFIX = "/admin/assets/" + generateVersion();

    public static final String ADMIN_APPLICATIONS_URI_PREFIX = "/admin/portal/admin/draft/_/adminapp";

    public static final String ADMIN_APPLICATIONS_PORTAL_URI_PREFIX = "/portal/draft/_/adminapp";

    public static final String generateAdminApplicationUri( String application, String adminApplication )
    {
        String uri = ADMIN_APPLICATIONS_URI_PREFIX + "/" + application + "/" + adminApplication;
        return ServletRequestUrlHelper.createUri( uri );
    }

    public static final String generateAdminApplicationPortalUri( String application, String adminApplication )
    {
        String uri = ADMIN_APPLICATIONS_PORTAL_URI_PREFIX + "/" + application + "/" + adminApplication;
        return ServletRequestUrlHelper.createUri( uri );
    }


    private static final String generateVersion()
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
