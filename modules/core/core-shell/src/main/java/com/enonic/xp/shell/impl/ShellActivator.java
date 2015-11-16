package com.enonic.xp.shell.impl;

import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

@Component(immediate = true, configurationPid = "com.enonic.xp.shell")
public final class ShellActivator
{
    private final static Logger LOG = LoggerFactory.getLogger( ShellActivator.class );

    private BundleContext context;

    private List<BundleActivator> activators;

    @Activate
    public void activate( final BundleContext context, final ShellConfig config )
        throws Exception
    {
        this.activators = Lists.newArrayList();

        if ( !config.enabled() )
        {
            LOG.info( "Remote shell access is disabled" );
            return;
        }

        final ShellContextProxy builder = new ShellContextProxy( context );
        builder.property( "gosh.args", "--nointeractive" );
        builder.property( "osgi.shell.telnet.ip", config.telnet_ip() );
        builder.property( "osgi.shell.telnet.port", String.valueOf( config.telnet_port() ) );
        builder.property( "osgi.shell.telnet.maxconn", String.valueOf( config.telnet_maxConnect() ) );
        builder.property( "osgi.shell.telnet.socketTimeout", String.valueOf( config.telnet_socketTimeout() ) );
        this.context = builder.build();

        startActivator( new org.apache.felix.shell.impl.Activator() );
        startActivator( new org.apache.felix.gogo.command.Activator() );
        startActivator( new org.apache.felix.gogo.runtime.activator.Activator() );
        startActivator( new org.apache.felix.shell.remote.Activator() );
        startActivator( new org.apache.felix.gogo.shell.Activator() );

        LOG.info( "Remote shell access is enabled (port = {})", config.telnet_port() );
    }

    @Deactivate
    public void deactivate()
        throws Exception
    {
        for ( final BundleActivator activator : this.activators )
        {
            activator.stop( this.context );
        }
    }

    private void startActivator( final BundleActivator activator )
        throws Exception
    {
        this.activators.add( activator );
        activator.start( this.context );
    }
}
