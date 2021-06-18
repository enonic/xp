package com.enonic.xp.server.internal.deploy;

import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationService;

@Component(configurationPid = "com.enonic.xp.server.deploy", service = AutoDeployer.class)
public final class AutoDeployer
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AutoDeployer.class );

    private ApplicationService applicationService;

    private Set<URL> urlSet;

    @Activate
    public void activate( final Map<String, String> config )
    {
        urlSet = findDeployList( config );
    }

    public void deploy()
    {
        for ( final URL url : urlSet )
        {
            try
            {
                deploy( url );
            }
            catch ( Exception e )
            {
                LOGGER.error( "Failed to install global application [" + url + "]", e );
            }
        }
    }

    private Set<URL> findDeployList( final Map<String, String> config )
    {
        return config.entrySet().stream().
            filter( e -> e.getKey().startsWith( "deploy." ) ).
            map( e -> toUrl( e.getValue() ) ).
            filter( Objects::nonNull ).
            collect( Collectors.toSet() );
    }

    private URL toUrl( final String value )
    {
        try
        {
            return new URL( value );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    private void deploy( final URL url )
    {
        DeployHelper.runAsAdmin( () -> doDeploy( url ) );
    }

    private void doDeploy( final URL url )
    {
        this.applicationService.installGlobalApplication( url );
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

}
