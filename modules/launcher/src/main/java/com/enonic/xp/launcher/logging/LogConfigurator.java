package com.enonic.xp.launcher.logging;

import java.io.File;

import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;

import com.enonic.xp.launcher.env.Environment;

public final class LogConfigurator
{
    private final Environment env;

    public LogConfigurator( final Environment env )
    {
        this.env = env;
    }

    public void configure()
    {
        SLF4JBridgeHandler.install();

        final File configFile = getConfigFile();
        if ( configFile.isFile() )
        {
            configure( configFile );
        }
    }

    private File getConfigFile()
    {
        final File configDir = new File( this.env.getHomeDir(), "config" );
        return new File( configDir, "logback.xml" );
    }

    private void configure( final File file )
    {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        try
        {
            doConfigure( context, file );
        }
        catch ( final Exception e )
        {
            // StatusPrinter will handle this
        }

        StatusPrinter.printInCaseOfErrorsOrWarnings( context );
    }

    private void doConfigure( final LoggerContext context, final File file )
        throws Exception
    {
        final JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext( context );
        context.reset();
        configurator.doConfigure( file );
    }
}
