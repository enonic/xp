package com.enonic.xp.impl.server.rest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallParams;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallResultJson;
import com.enonic.xp.impl.server.rest.model.ApplicationInstalledJson;

@Component(service = ApplicationResourceService.class)
public class ApplicationResourceService
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationResourceService.class );

    private final ApplicationService applicationService;

    private final EventPublisher eventPublisher;

    ApplicationLoader applicationLoader = new ApplicationLoader();

    @Activate
    public ApplicationResourceService( final @Reference ApplicationService applicationService, final @Reference EventPublisher eventPublisher )
    {
        this.applicationService = applicationService;
        this.eventPublisher = eventPublisher;
    }

    public ApplicationInstallResultJson installUrl( final ApplicationInstallParams params )
    {
        final ApplicationInstallResultJson result = new ApplicationInstallResultJson();
        try
        {
            final ByteSource source = applicationLoader.load( params.getUrl(), params.getSha512(), eventPublisher::publish );
            final Application application = this.applicationService.installGlobalApplication( source );

            result.setApplicationInstalledJson( new ApplicationInstalledJson( application, false ) );
            return result;
        }
        catch ( Exception e )
        {
            final String failure = "Failed to upload application from " + params.getUrl();
            LOG.error( failure, e );
            result.setFailure( failure );
            return result;
        }
    }
}
