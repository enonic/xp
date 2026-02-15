package com.enonic.xp.lib.portal.url;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.portal.url.ComponentUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ComponentUrlHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> urlServiceSupplier;

    private String id;

    private String path;

    private String type;

    private String component;

    private Map<String, List<String>> queryParams;

    public void setId( final String id )
    {
        this.id = id;
    }

    public void setPath( final String path )
    {
        this.path = path;
    }

    public void setUrlType( final String type )
    {
        this.type = type;
    }

    public void setComponent( final String component )
    {
        this.component = component;
    }

    public void setQueryParams( final ScriptValue params )
    {
        this.queryParams = UrlHandlerHelper.resolveQueryParams( params );
    }

    public String createUrl()
    {
        final ComponentUrlParams params =
            new ComponentUrlParams().id( this.id ).path( this.path ).type( this.type ).component( this.component );

        if ( this.queryParams != null )
        {
            this.queryParams.forEach( params::param );
        }

        return urlServiceSupplier.get().componentUrl( params );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }
}
