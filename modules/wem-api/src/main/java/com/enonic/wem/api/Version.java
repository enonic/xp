package com.enonic.wem.api;

public final class Version
{
    private final static String BANNER = "" +
        "        )    )     ) (                          *     \n" +
        "     ( /( ( /(  ( /( )\\ )  (     (  (         (  `    \n" +
        " (   )\\()))\\()) )\\()|()/(  )\\    )\\))(   '(   )\\))(   \n" +
        " )\\ ((_)\\((_)\\ ((_)\\ /(_)|((_)  ((_)()\\ ) )\\ ((_)()\\  \n" +
        "((_) _((_) ((_) _((_|_)) )\\___  _(())\\_)(|(_)(_()((_) \n" +
        "| __| \\| |/ _ \\| \\| |_ _((/ __| \\ \\((_)/ / __|  \\/  | \n" +
        "| _|| .` | (_) | .` || | | (__   \\ \\/\\/ /| _|| |\\/| | \n" +
        "|___|_|\\_|\\___/|_|\\_|___| \\___|   \\_/\\_/ |___|_|  |_|";

    private final static Version INSTANCE = new Version();

    private final String version;

    private Version()
    {
        final String value = getClass().getPackage().getImplementationVersion();
        this.version = value != null ? value : "x.x.x";
    }

    public String getName()
    {
        return "Enonic WEM";
    }

    public String getCopyright()
    {
        return "Copyright (c) 2000-2013 Enonic AS";
    }

    public String getVersion()
    {
        return this.version;
    }

    public String getBanner()
    {
        return BANNER;
    }

    @Override
    public String toString()
    {
        return getName() + " " + getVersion();
    }

    public static Version get()
    {
        return INSTANCE;
    }
}
