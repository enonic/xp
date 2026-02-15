package com.enonic.xp.lib.portal.url;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ServiceUrlParams;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ServiceUrlHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> urlServiceSupplier;

    private String application;

    private String service;

    private String urlType;

    private Map<String, List<String>> queryParams;

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public void setService( final String service )
    {
        this.service = service;
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
        final ServiceUrlParams params = new ServiceUrlParams().service( this.service ).application( this.application ).type( this.urlType );

        if ( this.queryParams != null )
        {
            this.queryParams.forEach( params::param );
        }

        return this.urlServiceSupplier.get().serviceUrl( params );
    }
}
