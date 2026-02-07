package com.enonic.xp.admin.impl.portal.widget;

import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@Component(immediate = true, service = UniversalApiHandler.class, property = {"applicationKey=admin", "apiKey=widget",
    "allowedPrincipals=role:system.admin.login", "displayName=Widget API"})
public class WidgetDispatcherApiHandler
    implements UniversalApiHandler
{
    private static final String WIDGET_API_BASE = "/_/admin:widget";

    private final GetListAllowedWidgetsHandler listWidgetsHandler;

    private final GetWidgetIconHandler getWidgetIconHandler;

    private final WidgetApiHandler widgetApiHandler;

    @Activate
    public WidgetDispatcherApiHandler( @Reference final GetListAllowedWidgetsHandler listWidgetsHandler,
                                       @Reference final GetWidgetIconHandler getWidgetIconHandler,
                                       @Reference final WidgetApiHandler widgetApiHandler )
    {
        this.listWidgetsHandler = listWidgetsHandler;
        this.getWidgetIconHandler = getWidgetIconHandler;
        this.widgetApiHandler = widgetApiHandler;
    }

    @Override
    public WebResponse handle( final WebRequest webRequest )
    {
        final String path = Objects.requireNonNull( webRequest.getEndpointPath(), "Endpoint path cannot be null" );

        if ( HttpMethod.GET.equals( webRequest.getMethod() ) && path.equals( WIDGET_API_BASE ) )
        {
            final Multimap<String, String> params = webRequest.getParams();

            if ( params.containsKey( "widgetInterface" ) )
            {
                return listWidgetsHandler.handle( webRequest );
            }
            else if ( params.containsKey( "icon" ) )
            {
                return getWidgetIconHandler.handle( webRequest );
            }
            else
            {
                throw WebException.notFound( "Widget API not found" );
            }
        }
        else
        {
            return widgetApiHandler.handle( webRequest );
        }
    }

}
