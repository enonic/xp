package com.enonic.xp.impl.server.rest;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallParams;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallResultJson;
import com.enonic.xp.impl.server.rest.model.ApplicationInstalledJson;
import com.enonic.xp.impl.server.rest.model.ApplicationParams;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.util.HexEncoder;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

@Path("/app")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class ApplicationResource
    implements JaxRsComponent
{
    private static final Set<String> ALLOWED_PROTOCOLS = Set.of( "http", "https" );

    private static final Logger LOG = LoggerFactory.getLogger( ApplicationResource.class );

    private ApplicationService applicationService;

    @POST
    @Path("install")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ApplicationInstallResultJson install( final MultipartForm form )
    {
        final MultipartItem appFile = form.get( "file" );

        if ( appFile == null )
        {
            throw new RuntimeException( "Missing file item" );
        }

        final ByteSource byteSource = appFile.getBytes();

        return installApplication( byteSource, appFile.getFileName() );
    }

    @POST
    @Path("installUrl")
    @Consumes(MediaType.APPLICATION_JSON)
    public ApplicationInstallResultJson installUrl( final ApplicationInstallParams params )
    {
        final String urlString = params.getUrl();
        final byte[] sha512 = Optional.ofNullable( params.getSha512() ).map( HexEncoder::fromHex ).orElse( null );
        final ApplicationInstallResultJson result = new ApplicationInstallResultJson();
        String failure;
        try
        {
            final URL url = new URL( urlString );

            if ( ALLOWED_PROTOCOLS.contains( url.getProtocol() ) )
            {
                return installApplication( url, sha512 );
            }
            else
            {
                failure = "Illegal protocol: " + url.getProtocol();
                result.setFailure( failure );

                return result;
            }

        }
        catch ( IOException e )
        {
            LOG.error( failure = "Failed to upload application from " + urlString, e );
            result.setFailure( failure );
            return result;
        }
    }

    @POST
    @Path("uninstall")
    @Consumes(MediaType.APPLICATION_JSON)
    public void uninstall( final ApplicationParams params )
    {
        this.applicationService.uninstallApplication( ApplicationKey.from( params.getKey() ), true );
    }

    @POST
    @Path("start")
    @Consumes(MediaType.APPLICATION_JSON)
    public void start( final ApplicationParams params )
    {
        this.applicationService.startApplication( ApplicationKey.from( params.getKey() ), true );
    }

    @POST
    @Path("stop")
    @Consumes(MediaType.APPLICATION_JSON)
    public void stop( final ApplicationParams params )
    {
        this.applicationService.stopApplication( ApplicationKey.from( params.getKey() ), true );
    }

    private ApplicationInstallResultJson installApplication( final URL url, byte[] sha512 )
    {
        final ApplicationInstallResultJson result = new ApplicationInstallResultJson();

        try
        {
            final Application application = this.applicationService.installGlobalApplication( url, sha512 );

            result.setApplicationInstalledJson( new ApplicationInstalledJson( application, false ) );
        }
        catch ( Exception e )
        {
            final String failure = "Failed to process application from " + url;
            LOG.error( failure, e );

            result.setFailure( failure );
        }
        return result;
    }

    private ApplicationInstallResultJson installApplication( final ByteSource byteSource, final String applicationName )
    {
        final ApplicationInstallResultJson result = new ApplicationInstallResultJson();

        try
        {
            final Application application = this.applicationService.installGlobalApplication( byteSource, applicationName );

            result.setApplicationInstalledJson( new ApplicationInstalledJson( application, false ) );
        }
        catch ( Exception e )
        {
            final String failure = "Failed to process application " + applicationName;
            LOG.error( failure, e );

            result.setFailure( failure );
        }
        return result;
    }

    @SuppressWarnings("UnusedDeclaration")
    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }
}
