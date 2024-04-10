package com.enonic.xp.portal.impl.handler.api;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.portal.impl.api.ApiConfig;
import com.enonic.xp.portal.impl.api.ApiIndexMode;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class, configurationPid = "com.enonic.xp.api")
public class ApiHandler
    extends BaseWebHandler
{
    private static final Pattern URL_PATTERN = Pattern.compile( "^/api$" );

    private static final ApplicationKey WELCOME_APP_KEY = ApplicationKey.from( "com.enonic.xp.app.welcome" );

    private final ApplicationService applicationService;

    private final ResourceService resourceService;

    private volatile ApiIndexMode apiIndexMode;

    @Activate
    public ApiHandler( final @Reference ApplicationService applicationService, final @Reference ResourceService resourceService )
    {
        super( -49, EnumSet.of( HttpMethod.GET, HttpMethod.OPTIONS ) );
        this.applicationService = applicationService;
        this.resourceService = resourceService;
    }

    @Activate
    @Modified
    public void activate( final ApiConfig config )
    {
        this.apiIndexMode = ApiIndexMode.from( config.api_index_enabled() );
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        boolean isSDK = applicationService.get( WELCOME_APP_KEY ) != null;
        boolean isIndexEnabled = isSDK || RunMode.get() == RunMode.DEV
            ? apiIndexMode == ApiIndexMode.ON || apiIndexMode == ApiIndexMode.AUTO
            : apiIndexMode == ApiIndexMode.ON;
        return isIndexEnabled && URL_PATTERN.matcher( webRequest.getRawPath() ).matches();
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        return WebResponse.create().contentType( MediaType.JSON_UTF_8 ).body( Map.of( "resources", getApiResources() ) ).build();
    }

    private List<String> getApiResources()
    {
        List<String> result = new ArrayList<>();

        result.add( "media" );
        result.addAll( applicationService.getInstalledApplications().stream().filter( application -> {
            ResourceKey resourceKey = ResourceKey.from( application.getKey(), "api/api.js" );
            return resourceService.getResource( resourceKey ).exists();
        } ).map( application -> application.getKey().getName() ).collect( Collectors.toList() ) );

        return result;
    }
}
