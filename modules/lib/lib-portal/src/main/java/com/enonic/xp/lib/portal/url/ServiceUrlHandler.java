package com.enonic.xp.lib.portal.url;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.portal.PortalRequestAccessor;
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

    private Map<String, Collection<String>> queryParams;

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

    public void addQueryParams( final ScriptValue params )
    {
        this.queryParams = UrlHandlerHelper.resolveQueryParams( params );
    }

    public String createUrl()
    {
        final ServiceUrlParams params = new ServiceUrlParams().service( this.service )
            .application( this.application )
            .portalRequest( PortalRequestAccessor.get() )
            .type( this.urlType );

        if ( this.queryParams != null )
        {
            this.queryParams.forEach( ( key, values ) -> values.forEach( value -> params.param( key, value ) ) );
        }

        return this.urlServiceSupplier.get().serviceUrl( params );
    }
}
