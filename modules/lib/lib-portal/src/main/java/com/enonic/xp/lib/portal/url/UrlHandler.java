package com.enonic.xp.lib.portal.url;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.portal.url.GenerateUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class UrlHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> urlServiceSupplier;

    private String path;

    private List<String> pathSegments;

    private String urlType;

    private Map<String, Collection<String>> queryParams;

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public void setPath( final Object value )
    {
        if ( value instanceof ScriptValue scriptValue && scriptValue.isArray() )
        {
            this.pathSegments = scriptValue.getArray( String.class );
        }
        else if ( value instanceof String )
        {
            this.path = (String) value;
        }
        else
        {
            throw new IllegalArgumentException( "Invalid path value" );
        }
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
        final GenerateUrlParams params = new GenerateUrlParams().url( this.path ).pathSegments( this.pathSegments ).type( this.urlType );

        if ( this.queryParams != null )
        {
            this.queryParams.forEach( ( key, values ) -> values.forEach( value -> params.param( key, value ) ) );
        }

        return this.urlServiceSupplier.get().generateUrl( params );
    }
}
