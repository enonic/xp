package com.enonic.xp.lib.portal.url;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ApiUrlHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> urlServiceSupplier;

    private ApplicationKey applicationKey;

    private String api;

    private String type;

    private String baseUrl;

    private String path;

    private List<String> pathSegments;

    private Map<String, Collection<String>> queryParams;

    @Override
    public void initialize( final BeanContext context )
    {
        this.applicationKey = context.getApplicationKey();
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public String createUrl()
    {
        final DescriptorKey descriptorKey = resolveDescriptorKey();

        final ApiUrlParams.Builder builder = ApiUrlParams.create()
            .setDescriptorKey( descriptorKey )
            .setType( this.type )
            .setBaseUrl( this.baseUrl )
            .setPath( this.path )
            .setPathSegments( this.pathSegments );

        if ( queryParams != null )
        {
            builder.addQueryParams( this.queryParams );
        }

        final ApiUrlParams params = builder.build();

        return this.urlServiceSupplier.get().apiUrl( params );
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

    private DescriptorKey resolveDescriptorKey()
    {
        if ( api.contains( ":" ) )
        {
            return DescriptorKey.from( api );
        }
        else
        {
            final PortalRequest portalRequest = PortalRequestAccessor.get();

            if ( portalRequest == null )
            {
                return DescriptorKey.from( applicationKey, api );
            }
            else
            {
                return DescriptorKey.from( Objects.requireNonNullElse( portalRequest.getApplicationKey(), applicationKey ), api );
            }
        }
    }
}
