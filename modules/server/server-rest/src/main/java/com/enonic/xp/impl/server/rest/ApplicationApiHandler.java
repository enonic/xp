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
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@Component(property = {"applicationKey=server", "apiKey=app", "displayName=Applications API", "allowedPrincipals=role:system.admin"})
public class ApplicationApiHandler
    implements UniversalApiHandler
{
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperHelper.create();

    private final ApplicationResourceService applicationResourceService;

    @Activate
    public ApplicationApiHandler( @Reference final ApplicationResourceService applicationResourceService )
    {
        this.applicationResourceService = applicationResourceService;
    }

    @Override
    public WebResponse handle( final WebRequest request )
    {
        final String subPath = request.getEndpointPath();
        if ( "/installUrl".equals( subPath ) )
        {
            if ( request.getMethod() != HttpMethod.POST )
            {
                return WebResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
            }
            try
            {
                final ApplicationInstallParams params =
                    OBJECT_MAPPER.readValue( request.getBodyAsString(), ApplicationInstallParams.class );
                final ApplicationInstallResultJson result = applicationResourceService.installUrl( params );

                return WebResponse.create()
                    .status( HttpStatus.OK )
                    .contentType( MediaType.JSON_UTF_8 )
                    .body( OBJECT_MAPPER.writeValueAsString( result ) )
                    .build();
            }
            catch ( JsonProcessingException e )
            {
                return WebResponse.create().status( HttpStatus.BAD_REQUEST ).build();
            }
        }

        return WebResponse.create().status( HttpStatus.NOT_FOUND ).build();
    }
}
