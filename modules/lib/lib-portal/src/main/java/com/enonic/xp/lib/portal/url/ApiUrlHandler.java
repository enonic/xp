package com.enonic.xp.lib.portal.url;

import java.util.Collection;
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

    private String application;

    private String api;

    private String type;

    private Boolean offline;

    private String projectName;

    private String branch;

    private String baseUrlKey;

    private String path;

    private List<String> pathSegments;

    private Map<String, Collection<String>> queryParams;

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public String createUrl()
    {
        final ApiUrlParams.Builder builder = ApiUrlParams.create()
            .setApplication( this.application )
            .setApi( this.api )
            .setType( this.type )
            .setOffline( this.offline )
            .setProjectName( this.projectName )
            .setBranch( this.branch )
            .setBaseUrlKey( this.baseUrlKey )
            .setPath( this.path )
            .setPathSegments( this.pathSegments );

        if ( queryParams != null )
        {
            builder.addQueryParams( this.queryParams );
        }

        final ApiUrlParams params = builder.build();

        return this.urlServiceSupplier.get().apiUrl( params );
    }

    public void setApplication( final String value )
    {
        this.application = value;
    }

    public void setApi( final String value )
    {
        this.api = value;
    }

    public void setUrlType( final String value )
    {
        this.type = value;
    }

    public void setOffline( final Boolean value )
    {
        this.offline = value;
    }

    public void setProjectName( final String value )
    {
        this.projectName = value;
    }

    public void setBranch( final String value )
    {
        this.branch = value;
    }

    public void setBaseUrlKey( final String value )
    {
        this.baseUrlKey = value;
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

    public void addQueryParams( final ScriptValue params )
    {
        this.queryParams = UrlHandlerHelper.resolveQueryParams( params );
    }
}
