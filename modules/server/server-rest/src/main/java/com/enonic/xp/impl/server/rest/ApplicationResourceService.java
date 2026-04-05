package com.enonic.xp.impl.server.rest;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.impl.server.rest.model.ApplicationActionResultJson;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallParams;
import com.enonic.xp.impl.server.rest.model.ApplicationInfoJson;
import com.enonic.xp.impl.server.rest.model.ApplicationParams;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

@Component(service = ApplicationResourceService.class)
public class ApplicationResourceService
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationResourceService.class );

    private final ApplicationService applicationService;

    private final ApplicationDescriptorService applicationDescriptorService;

    private final EventPublisher eventPublisher;

    ApplicationLoader applicationLoader = new ApplicationLoader();

    @Activate
    public ApplicationResourceService( final @Reference ApplicationService applicationService,
                                       final @Reference ApplicationDescriptorService applicationDescriptorService,
                                       final @Reference EventPublisher eventPublisher )
    {
        this.applicationService = applicationService;
        this.applicationDescriptorService = applicationDescriptorService;
        this.eventPublisher = eventPublisher;
    }

    public ApplicationInfoJson install( final MultipartForm form )
    {
        final MultipartItem appFile = form.get( "file" );
        if ( appFile == null )
        {
            throw WebException.badRequest( "Missing file item" );
        }

        final Application application = this.applicationService.installGlobalApplication( appFile.getBytes() );
        return ApplicationInfoJson.create( application, applicationDescriptorService.get( application.getKey() ), false );
    }

    public ApplicationInfoJson installUrl( final ApplicationInstallParams params )
    {
        final ByteSource source = applicationLoader.load( params.getUrl(), params.getSha512(), eventPublisher::publish );
        final Application application = this.applicationService.installGlobalApplication( source );
        return ApplicationInfoJson.create( application, applicationDescriptorService.get( application.getKey() ), false );
    }

    public ApplicationActionResultJson start( final ApplicationParams params )
    {
        final List<ApplicationActionResultJson.ActionResult> results = new ArrayList<>();
        for ( final String key : params.getKey() )
        {
            try
            {
                this.applicationService.startApplication( ApplicationKey.from( key ) );
                results.add( new ApplicationActionResultJson.ActionResult( key, true ) );
            }
            catch ( Exception e )
            {
                LOG.warn( "Failed to start application [{}]", key, e );
                results.add( new ApplicationActionResultJson.ActionResult( key, false ) );
            }
        }
        return new ApplicationActionResultJson( results );
    }

    public ApplicationActionResultJson stop( final ApplicationParams params )
    {
        final List<ApplicationActionResultJson.ActionResult> results = new ArrayList<>();
        for ( final String key : params.getKey() )
        {
            try
            {
                this.applicationService.stopApplication( ApplicationKey.from( key ) );
                results.add( new ApplicationActionResultJson.ActionResult( key, true ) );
            }
            catch ( Exception e )
            {
                LOG.warn( "Failed to stop application [{}]", key, e );
                results.add( new ApplicationActionResultJson.ActionResult( key, false ) );
            }
        }
        return new ApplicationActionResultJson( results );
    }

    public List<ApplicationInfoJson> getInstalledApplications()
    {
        return applicationService.getInstalledApplications().stream()
            .map( application -> ApplicationInfoJson.create( application, applicationDescriptorService.get( application.getKey() ),
                                                             applicationService.isLocalApplication( application.getKey() ) ) )
            .toList();
    }

    public ApplicationInfoJson getInstalledApplication( final ApplicationKey applicationKey )
    {
        final Application application = applicationService.getInstalledApplication( applicationKey );
        if ( application == null )
        {
            return null;
        }
        return ApplicationInfoJson.create( application, applicationDescriptorService.get( applicationKey ),
                                        applicationService.isLocalApplication( application.getKey() ) );
    }

    public ApplicationActionResultJson uninstall( final ApplicationParams params )
    {
        final List<ApplicationActionResultJson.ActionResult> results = new ArrayList<>();
        for ( final String key : params.getKey() )
        {
            try
            {
                this.applicationService.uninstallApplication( ApplicationKey.from( key ) );
                results.add( new ApplicationActionResultJson.ActionResult( key, true ) );
            }
            catch ( Exception e )
            {
                LOG.warn( "Failed to uninstall application [{}]", key, e );
                results.add( new ApplicationActionResultJson.ActionResult( key, false ) );
            }
        }
        return new ApplicationActionResultJson( results );
    }
}
