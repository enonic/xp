package com.enonic.xp.lib.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class WidgetUrlHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> portalUrlServiceSupplier;

    private ApplicationKey application;

    private String widget;

    private String urlType;

    private Map<String, Collection<String>> queryParams;

    @Override
    public void initialize( final BeanContext context )
    {
        this.portalUrlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public void setApplication( final String application )
    {
        this.application = ApplicationKey.from( application );
    }

    public void setWidget( final String widget )
    {
        this.widget = widget;
    }

    public void setUrlType( final String urlType )
    {
        this.urlType = urlType;
    }

    public void addQueryParams( final ScriptValue params )
    {
        if ( params == null || !params.isObject() )
        {
            return;
        }

        this.queryParams = new LinkedHashMap<>();

        for ( final Map.Entry<String, Object> param : params.getMap().entrySet() )
        {
            final Object value = param.getValue();
            final Collection<String> keyValues = this.queryParams.computeIfAbsent( param.getKey(), k -> new ArrayList<>() );
            if ( value instanceof Iterable<?> values )
            {
                for ( final Object v : values )
                {
                    keyValues.add( v.toString() );
                }
            }
            else
            {
                keyValues.add( value.toString() );
            }
        }
    }

    public String createUrl()
    {
        final ApiUrlParams.Builder builder = ApiUrlParams.create()
            .setDescriptorKey( DescriptorKey.from( ApplicationKey.from( "admin" ), "widget" ) )
            .setType( this.urlType )
            .setPathSegments( List.of( this.application.getName(), this.widget ) );

        if ( this.queryParams != null )
        {
            builder.addQueryParams( this.queryParams );
        }

        return this.portalUrlServiceSupplier.get().apiUrl( builder.build() );
    }
}
