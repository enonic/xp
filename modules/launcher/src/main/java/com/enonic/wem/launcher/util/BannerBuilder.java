package com.enonic.wem.launcher.util;

import com.enonic.wem.launcher.home.HomeDir;

import static com.google.common.base.StandardSystemProperty.JAVA_VERSION;
import static com.google.common.base.StandardSystemProperty.JAVA_VM_NAME;
import static com.google.common.base.StandardSystemProperty.JAVA_VM_VENDOR;
import static com.google.common.base.StandardSystemProperty.OS_ARCH;
import static com.google.common.base.StandardSystemProperty.OS_NAME;
import static com.google.common.base.StandardSystemProperty.OS_VERSION;

public final class BannerBuilder
{
    private final static String BANNER = "" +
        " _____ _____ _____ _____ _____ _____    _ _ _ _____ _____ \n" +
        "|   __|   | |     |   | |     |     |  | | | |   __|     |\n" +
        "|   __| | | |  |  | | | |-   -|   --|  | | | |   __| | | |\n" +
        "|_____|_|___|_____|_|___|_____|_____|  |_____|_____|_|_|_|\n";

    private final static String PRODUCT = "Enonic WEM";

    private final static String VERSION = "5.0.0-SNAPSHOT";

    private HomeDir homeDir;

    public BannerBuilder homeDir( final HomeDir homeDir )
    {
        this.homeDir = homeDir;
        return this;
    }

    public String build()
    {
        final StringBuilder str = new StringBuilder();
        str.append( "\n" ).append( BANNER ).append( "\n" );
        str.append( " # " ).append( PRODUCT ).append( " " ).append( VERSION ).append( "\n" );
        str.append( " # " ).append( getFormattedJvmInfo() ).append( "\n" );
        str.append( " # " ).append( getFormattedOsInfo() ).append( "\n" );
        str.append( " # Home directory is " ).append( this.homeDir.toString() ).append( "\n" );
        return str.toString();
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
