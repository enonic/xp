package com.enonic.xp.admin.impl.portal.extension;

import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@Component(immediate = true, property = {"key=" + AdminExtensionDispatcherApiHandler.EXTENSIONS_API,
    "allowedPrincipals=role:system.admin.login", "displayName=Admin Extensions API"})
public class AdminExtensionDispatcherApiHandler
    implements UniversalApiHandler
{
    static final String EXTENSIONS_API = "admin:extension";

    private final GetListAllowedAdminExtensionsHandler listExtensionsHandler;

    private final GetAdminExtensionIconHandler extensionIconHandler;

    private final AdminExtensionApiHandler extensionApiHandler;

    @Activate
    public AdminExtensionDispatcherApiHandler( @Reference final GetListAllowedAdminExtensionsHandler listExtensionsHandler,
                                               @Reference final GetAdminExtensionIconHandler getWidgetIconHandler,
                                               @Reference final AdminExtensionApiHandler widgetApiHandler )
    {
        this.listExtensionsHandler = listExtensionsHandler;
        this.extensionIconHandler = getWidgetIconHandler;
        this.extensionApiHandler = widgetApiHandler;
    }

    @Override
    public WebResponse handle( final WebRequest webRequest )
    {
        Objects.requireNonNull( webRequest.getEndpointPath(), "Endpoint path cannot be null" );

        if ( HttpMethod.GET.equals( webRequest.getMethod() ) && WebHandlerHelper.findApiPath( webRequest, EXTENSIONS_API ).isEmpty() )
        {
            final Multimap<String, String> params = webRequest.getParams();

            if ( params.containsKey( "interface" ) )
            {
                return listExtensionsHandler.handle( webRequest );
            }
            else if ( params.containsKey( "icon" ) )
            {
                return extensionIconHandler.handle( webRequest );
            }
            else
            {
                throw WebException.notFound( "Extension API not found" );
            }
        }
        else
        {
            return extensionApiHandler.handle( webRequest );
        }
    }
}
