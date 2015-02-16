package com.enonic.xp.launcher.util;

import com.enonic.xp.launcher.env.Environment;

import static com.google.common.base.StandardSystemProperty.JAVA_VERSION;
import static com.google.common.base.StandardSystemProperty.JAVA_VM_NAME;
import static com.google.common.base.StandardSystemProperty.JAVA_VM_VENDOR;
import static com.google.common.base.StandardSystemProperty.OS_ARCH;
import static com.google.common.base.StandardSystemProperty.OS_NAME;
import static com.google.common.base.StandardSystemProperty.OS_VERSION;

public final class BannerPrinter
{
    private final static String BANNER = "" +
        "                        _                   \n" +
        "  ___ _ __   ___  _ __ (_) ___  __  ___ __  \n" +
        " / _ \\ '_ \\ / _ \\| '_ \\| |/ __| \\ \\/ / '_ \\ \n" +
        "|  __/ | | | (_) | | | | | (__   >  <| |_) |\n" +
        " \\___|_| |_|\\___/|_| |_|_|\\___| /_/\\_\\ .__/ \n" +
        "                                     |_|    \n";

    private final static String PRODUCT = "Enonic XP";

    private final Environment env;

    public BannerPrinter( final Environment env )
    {
        this.env = env;
    }

    private String getVersion()
    {
        final String value = getClass().getPackage().getImplementationVersion();
        return value != null ? value : "x.x.x";
    }

    public void printBanner()
    {
        System.out.println( BANNER );
        System.out.println( " # " + PRODUCT + " " + getVersion() );
        System.out.println( " # " + getFormattedJvmInfo() );
        System.out.println( " # " + getFormattedOsInfo() );
        System.out.println( " # Install directory is " + this.env.getInstallDir() );
        System.out.println( " # Home directory is " + this.env.getHomeDir() );
        System.out.println();
    }

    private String getFormattedJvmInfo()
    {
        return String.format( "%s %s (%s)", JAVA_VM_NAME.value(), JAVA_VERSION.value(), JAVA_VM_VENDOR.value() );
    }

    private String getFormattedOsInfo()
    {
        return String.format( "%s %s (%s)", OS_NAME.value(), OS_VERSION.value(), OS_ARCH.value() );
    }
}
