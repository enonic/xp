package com.enonic.xp.app.main;

import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

public final class UriScriptHelper
{
    private static final String ADMIN_TOOLS_URI_PREFIX = "/admin/tool";

    private String rewriteUri( final String uri )
    {
        return ServletRequestUrlHelper.createUri( uri );
    }

    public String generateAdminToolUri()
    {
        return rewriteUri( ADMIN_TOOLS_URI_PREFIX );
    }

    public String generateAdminToolUri( String application, String adminTool )
    {
        String uri = ADMIN_TOOLS_URI_PREFIX + "/" + application + "/" + adminTool;
        return rewriteUri( uri );
    }

    public String getInstallation()
    {
        return ServerInfo.get().getName();
    }
}
