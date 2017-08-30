package com.enonic.xp.server.internal.deploy;

import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationService;

@Component(immediate = true, configurationPid = "com.enonic.xp.server.deploy")
public final class AutoDeployer
{
    private ApplicationService applicationService;

    @Activate
    public void activate( final Map<String, String> config )
    {
        final Set<URL> set = findDeployList( config );
        set.forEach( this::deploy );
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
