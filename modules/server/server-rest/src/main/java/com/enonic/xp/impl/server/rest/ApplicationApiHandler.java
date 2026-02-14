package com.enonic.xp.impl.server.rest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallParams;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallResultJson;
import com.enonic.xp.impl.server.rest.model.ApplicationParams;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.multipart.MultipartService;

@Component(property = {"key=server:app", "displayName=Applications API", "allowedPrincipals=role:system.admin"})
public class ApplicationApiHandler
    implements UniversalApiHandler
{
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperHelper.create();

    private static final String APP_API = "server:app";

    private final ApplicationResourceService applicationResourceService;

    private final MultipartService multipartService;

    @Activate
    public ApplicationApiHandler( @Reference final ApplicationResourceService applicationResourceService,
                                  @Reference final MultipartService multipartService )
    {
        this.applicationResourceService = applicationResourceService;
        this.multipartService = multipartService;
    }

    @Override
    public WebResponse handle( final WebRequest request )
    {
        final String apiPath = WebHandlerHelper.findApiPath( request, APP_API );

        if ( request.getMethod() != HttpMethod.POST )
        {
            return WebResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
        }

        try
        {
            return switch ( apiPath )
            {
                case "/install" -> handleInstall( request );
                case "/installUrl" -> handleInstallUrl( request );
                case "/start" -> handleStart( request );
                case "/stop" -> handleStop( request );
                case "/uninstall" -> handleUninstall( request );
                default -> WebResponse.create().status( HttpStatus.NOT_FOUND ).build();
            };
        }
        catch ( JsonProcessingException e )
        {
            return WebResponse.create().status( HttpStatus.BAD_REQUEST ).build();
        }
    }

    private WebResponse handleInstall( final WebRequest request )
        throws JsonProcessingException
    {
        final ApplicationInstallResultJson result = applicationResourceService.install( multipartService.parse( request.getRawRequest() ) );

        return WebResponse.create()
            .status( HttpStatus.OK )
            .contentType( MediaType.JSON_UTF_8 )
            .body( OBJECT_MAPPER.writeValueAsString( result ) )
            .build();
    }

    private WebResponse handleInstallUrl( final WebRequest request )
        throws JsonProcessingException
    {
        final ApplicationInstallParams params = OBJECT_MAPPER.readValue( request.getBodyAsString(), ApplicationInstallParams.class );
        final ApplicationInstallResultJson result = applicationResourceService.installUrl( params );

        return WebResponse.create()
            .status( HttpStatus.OK )
            .contentType( MediaType.JSON_UTF_8 )
            .body( OBJECT_MAPPER.writeValueAsString( result ) )
            .build();
    }

    private WebResponse handleStart( final WebRequest request )
        throws JsonProcessingException
    {
        final ApplicationParams params = OBJECT_MAPPER.readValue( request.getBodyAsString(), ApplicationParams.class );
        applicationResourceService.start( params );
        return WebResponse.create().status( HttpStatus.OK ).build();
    }

    private WebResponse handleStop( final WebRequest request )
        throws JsonProcessingException
    {
        final ApplicationParams params = OBJECT_MAPPER.readValue( request.getBodyAsString(), ApplicationParams.class );
        applicationResourceService.stop( params );
        return WebResponse.create().status( HttpStatus.OK ).build();
    }

    private WebResponse handleUninstall( final WebRequest request )
        throws JsonProcessingException
    {
        final ApplicationParams params = OBJECT_MAPPER.readValue( request.getBodyAsString(), ApplicationParams.class );
        applicationResourceService.uninstall( params );
        return WebResponse.create().status( HttpStatus.OK ).build();
    }
}
