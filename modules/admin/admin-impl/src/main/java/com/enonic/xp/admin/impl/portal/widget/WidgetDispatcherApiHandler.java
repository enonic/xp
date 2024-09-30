package com.enonic.xp.admin.impl.portal.widget;

import java.util.Objects;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Multimap;

import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.universalapi.UniversalApiHandler;

import static com.enonic.xp.admin.impl.portal.widget.GetListAllowedWidgetsHandler.WIDGET_INTERFACE_PARAM;
import static com.enonic.xp.admin.impl.portal.widget.GetWidgetIconHandler.ICON_PARAM;

@Component(immediate = true, service = UniversalApiHandler.class, property = {"applicationKey=admin", "apiKey=widget",
    "allowedPrincipals=role:system.admin.login", "allowedPrincipals=role:system.admin"})
public class WidgetDispatcherApiHandler
    implements UniversalApiHandler
{
    private static final Pattern WIDGET_API_PATTERN = Pattern.compile( "^/(_|api)/admin/widget/?$" );

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
        final String path = Objects.requireNonNullElse( webRequest.getEndpointPath(), webRequest.getRawPath() );

        if ( WIDGET_API_PATTERN.matcher( path ).matches() )
        {
            final Multimap<String, String> params = webRequest.getParams();

            if ( params.containsKey( WIDGET_INTERFACE_PARAM ) )
            {
                return listWidgetsHandler.handle( webRequest );
            }
            else if ( params.containsKey( ICON_PARAM ) )
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
