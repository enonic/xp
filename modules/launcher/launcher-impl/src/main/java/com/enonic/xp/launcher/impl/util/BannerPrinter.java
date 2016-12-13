package com.enonic.xp.launcher.impl.util;

import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.launcher.VersionInfo;
import com.enonic.xp.launcher.impl.SharedConstants;
import com.enonic.xp.launcher.impl.env.Environment;

import static com.google.common.base.StandardSystemProperty.JAVA_VERSION;
import static com.google.common.base.StandardSystemProperty.JAVA_VM_NAME;
import static com.google.common.base.StandardSystemProperty.JAVA_VM_VENDOR;
import static com.google.common.base.StandardSystemProperty.OS_ARCH;
import static com.google.common.base.StandardSystemProperty.OS_NAME;
import static com.google.common.base.StandardSystemProperty.OS_VERSION;

public final class BannerPrinter
{
    private final static String PRODUCT = "Enonic XP";

    private final Environment env;

    private final VersionInfo version;

    public BannerPrinter( final Environment env, final VersionInfo version )
    {
        this.env = env;
        this.version = version;
    }

    public void printBanner()
    {
        System.out.println( loadBanner() );
        System.out.println( "# " + PRODUCT + " " + this.version.getVersion() );
        System.out.println( "# " + getFormattedBuildInfo() );
        System.out.println( "# " + getFormattedJvmInfo() );
        System.out.println( "# " + getFormattedOsInfo() );
        System.out.println( "# Install directory is " + this.env.getInstallDir() );
        System.out.println( "# Home directory is " + this.env.getHomeDir() );
        System.out.println();
        printWarnings();
    }

    private String getFormattedBuildInfo()
    {
        return String.format( "Built on %s (hash = %s, branch = %s)", this.version.getBuildTimestamp(), this.version.getBuildHash(),
                              this.version.getBuildBranch() );
    }

    private String getFormattedJvmInfo()
    {
        return String.format( "%s %s (%s)", JAVA_VM_NAME.value(), JAVA_VERSION.value(), JAVA_VM_VENDOR.value() );
    }

    private String getFormattedOsInfo()
    {
        return String.format( "%s %s (%s)", OS_NAME.value(), OS_VERSION.value(), OS_ARCH.value() );
    }

    private void printWarnings()
    {
        final boolean devMode = "dev".equalsIgnoreCase( System.getProperty( SharedConstants.XP_RUN_MODE ) );
        if ( devMode )
        {
            printDevModeWarning();
        }
    }

    private void printDevModeWarning()
    {
        System.out.println( "*" );
        System.out.println( "* DEV mode is ON. This will slow down the system and should NOT BE used in production." );
        System.out.println( "*" );
        System.out.println();
    }

    private String loadBanner()
    {
        try
        {
            final URL url = getClass().getResource( "banner.txt" );
            return Resources.toString( url, Charsets.UTF_8 );
        }
        catch ( final Exception e )
        {
            return "<<-- ERROR: No Banner! -->>";
        }
    }
}
