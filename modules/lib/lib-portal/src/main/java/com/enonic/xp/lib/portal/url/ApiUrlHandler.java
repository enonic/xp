package com.enonic.xp.lib.portal.url;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ApiUrlHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> urlServiceSupplier;

    private String api;

    private String type;

    private String baseUrl;

    private String path;

    private List<String> pathSegments;

    private Map<String, List<String>> queryParams;

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public String createUrl()
    {
        final ApiUrlParams.Builder builder = ApiUrlParams.create()
            .setApi( this.api )
            .setType( this.type )
            .setBaseUrl( this.baseUrl )
            .setPath( this.path )
            .setPathSegments( this.pathSegments );

        if ( queryParams != null )
        {
            builder.setQueryParams( this.queryParams );
        }
        return this.urlServiceSupplier.get().apiUrl( builder.build() );
    }

    public void setApi( final String value )
    {
        this.api = value;
    }

    public void setUrlType( final String value )
    {
        this.type = value;
    }

    public void setBaseUrl( final String baseUrl )
    {
        this.baseUrl = baseUrl;
    }

    public void setPath( final Object value )
    {
        switch ( value )
        {
            case String s -> this.path = s;
            case ScriptValue scriptValue when scriptValue.isArray() -> this.pathSegments = scriptValue.getArray( String.class );
            case null, default -> throw new IllegalArgumentException( "Invalid path value" );
        }
    }

    public void setQueryParams( final ScriptValue params )
    {
        this.queryParams = UrlHandlerHelper.resolveQueryParams( params );
    }
}
