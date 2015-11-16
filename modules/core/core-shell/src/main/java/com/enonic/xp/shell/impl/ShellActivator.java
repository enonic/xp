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

    private ShellConfig config;

    protected BundleContext context;

    protected final List<BundleActivator> activators;

    public ShellActivator()
    {
        this.activators = Lists.newArrayList();
        this.activators.add( new org.apache.felix.shell.impl.Activator() );
        this.activators.add( new org.apache.felix.gogo.command.Activator() );
        this.activators.add( new org.apache.felix.gogo.runtime.activator.Activator() );
        this.activators.add( new org.apache.felix.shell.remote.Activator() );
        this.activators.add( new org.apache.felix.gogo.shell.Activator() );
    }

    @Activate
    public void activate( final BundleContext context, final ShellConfig config )
        throws Exception
    {
        this.config = config;

        final ShellContextProxy builder = new ShellContextProxy( context );
        builder.property( "gosh.args", "--nointeractive" );
        builder.property( "osgi.shell.telnet.ip", this.config.telnet_ip() );
        builder.property( "osgi.shell.telnet.port", String.valueOf( this.config.telnet_port() ) );
        builder.property( "osgi.shell.telnet.maxconn", String.valueOf( this.config.telnet_maxConnect() ) );
        builder.property( "osgi.shell.telnet.socketTimeout", String.valueOf( this.config.telnet_socketTimeout() ) );
        this.context = builder.build();

        if ( !this.config.enabled() )
        {
            LOG.info( "Remote shell access is disabled" );
            return;
        }

        for ( final BundleActivator activator : this.activators )
        {
            activator.start( this.context );
        }

        LOG.info( "Remote shell access is enabled (port = {})", config.telnet_port() );
    }

    @Deactivate
    public void deactivate()
        throws Exception
    {
        if ( !this.config.enabled() )
        {
            return;
        }

        for ( final BundleActivator activator : this.activators )
        {
            activator.stop( this.context );
        }
    }
}
