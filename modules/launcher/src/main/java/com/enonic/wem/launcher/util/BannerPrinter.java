package com.enonic.wem.launcher.util;

import java.io.File;
import java.io.PrintStream;

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
        " _____ _____ _____ _____ _____ _____    _ _ _ _____ _____ \n" +
        "|   __|   | |     |   | |     |     |  | | | |   __|     |\n" +
        "|   __| | | |  |  | | | |-   -|   --|  | | | |   __| | | |\n" +
        "|_____|_|___|_____|_|___|_____|_____|  |_____|_____|_|_|_|\n";

    private final static String PRODUCT = "Enonic WEM";

    private final static String VERSION = "5.0.0-SNAPSHOT";

    private final PrintStream out;

    public BannerPrinter( final PrintStream out )
    {
        this.out = out;
    }

    public void printHeader()
    {
        this.out.println( BANNER );
        this.out.println( " # " + PRODUCT + " " + VERSION );
        this.out.println( " # " + getFormattedJvmInfo() );
        this.out.println( " # " + getFormattedOsInfo() );
    }

    public void printEnvironment( final Environment env )
    {
        this.out.println( " # Install directory is " + env.getInstallDir() );
        this.out.println( " # Home directory is " + env.getHomeDir() );
    }

    private String getFormattedJvmInfo()
    {
        return String.format( "%s %s (%s)", JAVA_VM_NAME.value(), JAVA_VERSION.value(), JAVA_VM_VENDOR.value() );
    }

    private String getFormattedOsInfo()
    {
        return String.format( "%s %s (%s)", OS_NAME.value(), OS_VERSION.value(), OS_ARCH.value() );
    }

    private String getFormattedDir( final File dir )
    {
        return dir != null ? dir.toString() : "<not set>";
    }
}
