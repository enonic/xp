package com.enonic.xp.admin.impl.portal.extension;

import java.util.Objects;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@Component(immediate = true, service = UniversalApiHandler.class, property = {"applicationKey=admin", "apiKey=extension",
    "allowedPrincipals=role:system.admin.login", "displayName=Admin Extensions API"})
public class AdminExtensionDispatcherApiHandler
    implements UniversalApiHandler
{
    private static final Pattern EXTENSION_API_PATTERN = Pattern.compile( "^/_/admin:extension/?$" );

    private final GetListAllowedAdminExtensionsHandler listExtensionsHandler;

    private final GetAdminExtensionIconHandler extensionIconHandler;

    private final AdminExtensionApiHandler extensionApiHandler;

    @Activate
    public AdminExtensionDispatcherApiHandler( @Reference final GetListAllowedAdminExtensionsHandler listExtensionsHandler,
                                               @Reference final GetAdminExtensionIconHandler extensionIconHandler,
                                               @Reference final AdminExtensionApiHandler extensionApiHandler )
    {
        this.listExtensionsHandler = listExtensionsHandler;
        this.extensionIconHandler = extensionIconHandler;
        this.extensionApiHandler = extensionApiHandler;
    }


    @Override
    public WebResponse handle( final WebRequest webRequest )
    {
        final String path = Objects.requireNonNull( webRequest.getEndpointPath(), "Endpoint path cannot be null" );

        if ( EXTENSION_API_PATTERN.matcher( path ).matches() && HttpMethod.GET.equals( webRequest.getMethod() ) )
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
