package com.enonic.wem.runner;

import java.io.File;
import java.io.InputStream;
import java.util.logging.LogManager;

import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;

final class Runner
{
    private final Tomcat tomcat;

    public Runner()
        throws Exception
    {
        setupLogging();
        this.tomcat = new Tomcat();
        init();
    }

    private void init()
    {
        try
        {
            doInit();
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public void start()
    {
        try
        {
            doStart();
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public void stop()
    {
        try
        {
            dostop();
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private void doInit()
        throws Exception
    {
        final String docBase = new File( "./modules/wem-webapp/src/main/webapp" ).getAbsolutePath();
        final String generatedDir = new File( "./modules/wem-webapp/target/main/webapp" ).getAbsolutePath();

        this.tomcat.setPort( 8080 );
        this.tomcat.setHostname( "localhost" );
        this.tomcat.setBaseDir( new File( "./modules/wem-runner/target/tomcat" ).getAbsolutePath() );

        final Context context = this.tomcat.addWebapp( "/", docBase );
        context.setJarScanner( new NopJarScanner() );
        context.setDocBase( docBase );

        final StandardRoot standardRoot = new StandardRoot( context );
        context.setResources( standardRoot );

        if ( new File( generatedDir ).isDirectory() )
        {
            standardRoot.createWebResourceSet( WebResourceRoot.ResourceSetType.PRE, "/", generatedDir, null, "/" );
        }
    }

    private void doStart()
        throws Exception
    {
        this.tomcat.start();
        this.tomcat.getServer().await();
    }

    private void dostop()
        throws Exception
    {
        this.tomcat.stop();
    }

    private void setupLogging()
        throws Exception
    {
        final InputStream in = getClass().getResourceAsStream( "/logging.properties" );
        LogManager.getLogManager().reset();
        LogManager.getLogManager().readConfiguration( in );
    }
}
