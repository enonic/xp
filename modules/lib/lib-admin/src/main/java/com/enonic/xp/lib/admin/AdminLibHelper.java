package com.enonic.xp.lib.admin;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.url.GenerateUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;

public final class AdminLibHelper
    implements ScriptBean
{
    private Supplier<PortalUrlService> portalUrlServiceSupplier;

    @Override
    public void initialize( final BeanContext context )
    {
        this.portalUrlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public String getInstallation()
    {
        return ServerInfo.get().getName();
    }

    public String getVersion()
    {
        return VersionInfo.get().getVersion();
    }

    public String getToolUrl( final String application, final String toolName )
    {
        final GenerateUrlParams params = new GenerateUrlParams();
        params.url( "/admin/" + ApplicationKey.from( application ) + "/" + toolName );

        return this.portalUrlServiceSupplier.get().generateUrl( params );
    }

    public String getHomeToolUrl( final String urlType )
    {
        final GenerateUrlParams params = new GenerateUrlParams();
        params.url( "/admin" );
        params.type( urlType );

        return this.portalUrlServiceSupplier.get().generateUrl( params );
    }
}
