package com.enonic.wem.web.boot;

import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.Version;
import com.enonic.wem.core.home.HomeDir;
import com.enonic.wem.core.home.HomeResolver;

final class BootEnvironment
{
    protected final Logger log;

    public BootEnvironment()
    {
        this.log = LoggerFactory.getLogger( getClass() );
    }

    public void initialize()
    {
        try
        {
            doInitialize();
        }
        catch ( final Exception e )
        {
            this.log.error( "Error occurred starting system", e );

            if ( e instanceof RuntimeException )
            {
                throw (RuntimeException) e;
            }
            else
            {
                throw new RuntimeException( e );
            }
        }
    }

    protected void doInitialize()
        throws Exception
    {
        setSystemProperties();
        resolveHomeDir();
        logBanner();
    }

    public void destroy()
    {
        // Do nothing for now
    }

    private void resolveHomeDir()
    {
        final HomeResolver resolver = new HomeResolver();
        resolver.addSystemProperties( System.getenv() );
        resolver.addSystemProperties( System.getProperties() );
        resolver.resolve();
    }

    private void logBanner()
    {
        final StringBuilder str = new StringBuilder();
        str.append( "\n" ).append( Version.get().getBanner() ).append( "\n" );
        str.append( "  # " ).append( Version.get().getNameAndVersion() ).append( "\n" );
        str.append( "  # " ).append( getFormattedJvmInfo() ).append( "\n" );
        str.append( "  # " ).append( getFormattedOsInfo() ).append( "\n" );
        str.append( "  # Home directory is " ).append( HomeDir.get() ).append( "\n" );

        this.log.info( str.toString() );
    }

    private String getFormattedJvmInfo()
    {
        final StringBuilder str = new StringBuilder();
        str.append( SystemUtils.JAVA_RUNTIME_NAME ).append( " " ).append( SystemUtils.JAVA_RUNTIME_VERSION ).append( " (" ).append(
            SystemUtils.JAVA_VENDOR ).append( ")" );
        return str.toString();
    }

    private String getFormattedOsInfo()
    {
        final StringBuilder str = new StringBuilder();
        str.append( SystemUtils.OS_NAME ).append( " " ).append( SystemUtils.OS_VERSION ).append( " (" ).append(
            SystemUtils.OS_ARCH ).append( ")" );
        return str.toString();
    }

    private void setSystemProperties()
    {
        System.setProperty( "java.awt.headless", "true" );
    }
}
