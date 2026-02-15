package com.enonic.xp.lib.portal.url;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class AssetUrlHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> urlServiceSupplier;

    private String path;

    private String urlType;

    private String application;

    private Map<String, List<String>> queryParams;

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public void setPath( final String path )
    {
        this.path = path;
    }

    public void setApplication( final String application )
    {
        this.application = application;
    }

    public void setUrlType( final String urlType )
    {
        this.urlType = urlType;
    }

    public void setQueryParams( final ScriptValue params )
    {
        this.queryParams = UrlHandlerHelper.resolveQueryParams( params );
    }

    public String createUrl()
    {
        final AssetUrlParams params = new AssetUrlParams().path( this.path ).application( this.application ).type( this.urlType );

        if ( this.queryParams != null )
        {
            this.queryParams.forEach( params::param );
        }

        return this.urlServiceSupplier.get().assetUrl( params );
    }
}
