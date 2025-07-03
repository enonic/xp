package com.enonic.xp.portal.impl.handler.api;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.google.common.net.MediaType;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.api.ApiDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.impl.api.ApiConfig;
import com.enonic.xp.portal.impl.api.ApiIndexMode;
import com.enonic.xp.portal.impl.api.DynamicUniversalApiHandlerRegistry;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
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

    private final ApiDescriptorService apiDescriptorService;

    private final DynamicUniversalApiHandlerRegistry universalApiHandlerRegistry;

    private volatile ApiIndexMode apiIndexMode;

    @Activate
    public ApiHandler( @Reference final ApplicationService applicationService, @Reference final ApiDescriptorService apiDescriptorService,
                       @Reference final DynamicUniversalApiHandlerRegistry universalApiHandlerRegistry )
    {
        super( -49, EnumSet.of( HttpMethod.GET, HttpMethod.OPTIONS ) );
        this.applicationService = applicationService;
        this.apiDescriptorService = apiDescriptorService;
        this.universalApiHandlerRegistry = universalApiHandlerRegistry;
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

    private List<Map<String, Object>> getApiResources()
    {
        final List<Map<String, Object>> result = new ArrayList<>();

        universalApiHandlerRegistry.getAllApiDescriptors()
            .stream()
            .filter( ApiDescriptor::isMount )
            .forEach( descriptor -> result.add( map( descriptor ) ) );

        applicationService.getInstalledApplications()
            .forEach( application -> apiDescriptorService.getByApplication( application.getKey() )
                .stream()
                .filter( ApiDescriptor::isMount )
                .forEach( descriptor -> result.add( map( descriptor ) ) ) );

        return result;
    }

    private Map<String, Object> map( final ApiDescriptor apiDescriptor )
    {
        final DescriptorKey descriptorKey = apiDescriptor.getKey();

        final Map<String, Object> result = new LinkedHashMap<>();

        result.put( "descriptor", descriptorKey.toString() );
        result.put( "application", descriptorKey.getApplicationKey().toString() );
        result.put( "name", descriptorKey.getName() );
        result.put( "allowedPrincipals", Objects.requireNonNullElseGet( apiDescriptor.getAllowedPrincipals(), PrincipalKeys::empty )
            .stream()
            .map( PrincipalKey::toString )
            .collect( Collectors.toList() ) );
        result.put( "displayName", apiDescriptor.getDisplayName() );
        result.put( "description", apiDescriptor.getDescription() );
        result.put( "documentationUrl", apiDescriptor.getDocumentationUrl() );
        result.put( "mount", apiDescriptor.isMount() );

        return result;
    }
}
