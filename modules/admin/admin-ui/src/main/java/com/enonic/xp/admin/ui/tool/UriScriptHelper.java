package com.enonic.xp.admin.ui.tool;


import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

public class UriScriptHelper
{
    public static final String ADMIN_ASSETS_URI_PREFIX = "/admin/assets/" + generateVersion();

    public static final String ADMIN_TOOLS_URI_PREFIX = "/admin/tool";

    public static final String generateAdminToolUri()
    {
        return ServletRequestUrlHelper.createUri( ADMIN_TOOLS_URI_PREFIX );
    }

    public static final String generateAdminToolUri( String application, String adminTool )
    {
        String uri = ADMIN_TOOLS_URI_PREFIX + "/" + application + "/" + adminTool;
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
