package com.enonic.xp.ignite.impl;

import java.util.Hashtable;
import java.util.function.Function;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.ignite.impl.config.ConfigurationFactory;
import com.enonic.xp.ignite.impl.config.IgniteSettings;

import static org.apache.ignite.IgniteSystemProperties.IGNITE_NO_ASCII;
import static org.apache.ignite.IgniteSystemProperties.IGNITE_NO_SHUTDOWN_HOOK;
import static org.apache.ignite.IgniteSystemProperties.IGNITE_PERFORMANCE_SUGGESTIONS_DISABLED;
import static org.apache.ignite.IgniteSystemProperties.IGNITE_TROUBLESHOOTING_LOGGER;
import static org.apache.ignite.IgniteSystemProperties.IGNITE_UPDATE_NOTIFIER;

@Component(immediate = true, configurationPid = "com.enonic.xp.ignite")
public class IgniteActivator
{
    private final ClusterConfig clusterConfig;

    private Ignite ignite;

    private ServiceRegistration<Ignite> igniteReg;

    private final Function<IgniteConfiguration, Ignite> ignitionStarter;

    @Activate
    public IgniteActivator( @Reference final ClusterConfig clusterConfig )
    {
        this( clusterConfig, Ignition::start );
    }

    IgniteActivator( final ClusterConfig clusterConfig, final Function<IgniteConfiguration, Ignite> ignitionStarter )
    {
        this.clusterConfig = clusterConfig;
        this.ignitionStarter = ignitionStarter;
    }

    @SuppressWarnings("unused")
    @Activate
    public void activate( final BundleContext context, final IgniteSettings igniteSettings )
    {
        if ( clusterConfig.isEnabled() )
        {
            adjustLoggingVerbosity();

            final IgniteConfiguration igniteConfig = ConfigurationFactory.create().
                clusterConfig( clusterConfig ).
                igniteConfig( igniteSettings ).
                bundleContext( context ).
                build().
                execute();

            System.setProperty( IGNITE_NO_SHUTDOWN_HOOK, "true" );

            final Thread thread = Thread.currentThread();
            final ClassLoader classLoader = thread.getContextClassLoader();
            try
            {
                thread.setContextClassLoader( Ignite.class.getClassLoader() );
                ignite = ignitionStarter.apply( igniteConfig );
            }
            finally
            {
                thread.setContextClassLoader( classLoader );
            }
            igniteReg = context.registerService( Ignite.class, ignite, new Hashtable<>() );
        }
    }

    @SuppressWarnings("unused")
    @Deactivate
    public void deactivate()
    {
        if ( ignite != null )
        {
            igniteReg.unregister();
            ignite.close();
        }
    }

    private static void adjustLoggingVerbosity()
    {
        System.setProperty( IGNITE_NO_ASCII, "false" );
        System.setProperty( IGNITE_PERFORMANCE_SUGGESTIONS_DISABLED, "true" );
        System.setProperty( IGNITE_UPDATE_NOTIFIER, "false" );
        System.setProperty( IGNITE_TROUBLESHOOTING_LOGGER, "false" );
    }
}
