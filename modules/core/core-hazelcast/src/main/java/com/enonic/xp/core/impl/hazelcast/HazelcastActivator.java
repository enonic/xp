package com.enonic.xp.core.impl.hazelcast;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.config.Config;
import com.hazelcast.osgi.HazelcastOSGiInstance;
import com.hazelcast.osgi.HazelcastOSGiService;

@Component(immediate = true)
public class HazelcastActivator
{
    private final HazelcastConfigService hazelcastConfigService;

    private final HazelcastOSGiService hazelcastOSGiService;

    private HazelcastOSGiInstance hazelcastInstance;

    @Activate
    public HazelcastActivator( @Reference final HazelcastOSGiService hazelcastOSGiService,
                               @Reference final HazelcastConfigService hazelcastConfigService )
    {
        this.hazelcastConfigService = hazelcastConfigService;
        this.hazelcastOSGiService = hazelcastOSGiService;
    }

    @Activate
    public void activate( final ComponentContext componentContext )
    {
        if ( hazelcastConfigService.isHazelcastEnabled() )
        {
            final Config config = hazelcastConfigService.configure();
            hazelcastInstance = hazelcastOSGiService.newHazelcastInstance( config );
        }

        // Enable condition when `HazelcastActivator` finishes activation, regardless if Hazelcast was actually started.
        // Guarantees that Hazelcast (if present) starts before Elasticsearch on XP startup.
        // Do not disable condition in `deactivate` as we only want to prevent initial startup deadlock.
        // See https://github.com/enonic/xp/issues/8446
        componentContext.enableComponent( HazelcastActivatorActivatedCondition.class.getName() );
    }

    @Deactivate
    public void deactivate()
    {
        if ( hazelcastInstance != null )
        {
            hazelcastOSGiService.shutdownHazelcastInstance( hazelcastInstance );
        }
    }
}
