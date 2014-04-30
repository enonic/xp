package com.enonic.wem.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.Version;
import com.enonic.wem.core.home.HomeDir;
import com.enonic.wem.core.home.HomeResolver;

import static com.google.common.base.StandardSystemProperty.JAVA_VENDOR;
import static com.google.common.base.StandardSystemProperty.JAVA_VERSION;
import static com.google.common.base.StandardSystemProperty.JAVA_VM_NAME;
import static com.google.common.base.StandardSystemProperty.OS_ARCH;
import static com.google.common.base.StandardSystemProperty.OS_NAME;
import static com.google.common.base.StandardSystemProperty.OS_VERSION;

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
        return String.format( "%s %s (%s)", JAVA_VM_NAME.value(), JAVA_VERSION.value(), JAVA_VENDOR.value() );
    }

    private String getFormattedOsInfo()
    {
        return String.format( "%s %s (%s)", OS_NAME.value(), OS_VERSION.value(), OS_ARCH.value() );
    }

    private void setSystemProperties()
    {
        System.setProperty( "java.awt.headless", "true" );
    }
}
